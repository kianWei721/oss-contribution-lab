import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Standalone lifecycle model for Apache RocketMQ PR #11157.
 *
 * It intentionally does not depend on RocketMQ classes. The purpose is to
 * validate time-axis invariants independently from the implementation:
 *   1) current PR's one-pass final-due cursor;
 *   2) periodic narrow-stripe scanning keyed by final due time.
 */
public class RocketMQ11157RollLifecycleProof {
    static final long HOUR = TimeUnit.HOURS.toMillis(1);
    static final long DAY = TimeUnit.DAYS.toMillis(1);

    static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }

    static List<Long> pr11157RollTimes(long sentAt, long dueAt, long maxDelay, long range) {
        long checkpoint = sentAt + maxDelay - range;
        List<Long> rolls = new ArrayList<>();
        long now = sentAt;
        for (int i = 0; i < 100000 && checkpoint <= dueAt + range; i++) {
            long triggerAt = checkpoint + range - maxDelay;
            now = Math.max(now, triggerAt);
            if (checkpoint <= dueAt && dueAt < checkpoint + range && now >= sentAt) {
                rolls.add(now);
            }
            checkpoint += range;
        }
        return rolls;
    }

    static List<Long> pr11157RollTimesWithIndexLag(long sentAt, long indexedAt, long dueAt,
                                                   long maxDelay, long range) {
        long checkpoint = sentAt + maxDelay - range;
        List<Long> rolls = new ArrayList<>();
        long now = sentAt;
        for (int i = 0; i < 100000 && checkpoint <= dueAt + range; i++) {
            long triggerAt = checkpoint + range - maxDelay;
            now = Math.max(now, triggerAt);
            if (now >= indexedAt && checkpoint <= dueAt && dueAt < checkpoint + range) {
                rolls.add(now);
            }
            checkpoint += range;
        }
        return rolls;
    }

    static List<Long> stripeRollTimes(long indexedAt, long dueAt,
                                      long maxDelay, long tick, long period) {
        long kMax = maxDelay / period;
        List<Long> events = new ArrayList<>();
        for (long k = 1; k <= kMax; k++) {
            long x = dueAt - k * period;
            long tickStart = Math.floorDiv(x, tick) * tick;
            if (tickStart >= indexedAt && tickStart < dueAt) {
                events.add(tickStart);
            }
        }
        Collections.sort(events);
        return events;
    }

    static long maxPointerAge(long sentAt, long dueAt, List<Long> successfulRolls) {
        long previousWrite = sentAt;
        long maxAge = 0;
        for (long roll : successfulRolls) {
            if (roll < sentAt || roll >= dueAt) continue;
            maxAge = Math.max(maxAge, roll - previousWrite);
            previousWrite = roll;
        }
        maxAge = Math.max(maxAge, dueAt - previousWrite);
        return maxAge;
    }

    static long safePeriod(long retention, long legacyRollWindow) {
        return Math.max(HOUR, Math.min(legacyRollWindow, retention / 2));
    }

    static void fixedCounterexamples() {
        long maxDelay = 72 * HOUR;
        long range = 2 * HOUR;
        long retention = 48 * HOUR;

        List<Long> rolls = pr11157RollTimes(0, 72 * HOUR, maxDelay, range);
        check(rolls.size() == 1, "PR model should roll max-delay record exactly once");
        long ageAtDue = maxPointerAge(0, 72 * HOUR, rolls);
        check(ageAtDue > retention,
            "one-pass refreshed CommitLog pointer should become older than 48h before 72h due time");

        List<Long> sixtyHour = pr11157RollTimesWithIndexLag(
            0, 10 * 60_000L, 60 * HOUR, maxDelay, range);
        check(sixtyHour.isEmpty(),
            "60h-delay record should be behind PR cursor and never rolled");
        check(60 * HOUR > retention,
            "60h due time exceeds 48h CommitLog retention");

        List<Long> late = pr11157RollTimesWithIndexLag(
            0, 10 * 60_000L, 71 * HOUR, maxDelay, range);
        check(late.isEmpty(),
            "late insertion into an already-scanned PR window must not be revisited");

        System.out.printf(Locale.ROOT,
            "PR_FIXED: maxDelay72h rolls=%d, pointerAgeAtDue=%.2fh; delay60h rolls=%d; late71h rolls=%d%n",
            rolls.size(), ageAtDue / (double) HOUR, sixtyHour.size(), late.size());
    }

    static void stripeFixedCases() {
        long maxDelay = 30 * DAY;
        long retention = 7 * DAY;
        long tick = HOUR;
        long legacyRollWindow = 2 * DAY;
        long period = safePeriod(retention, legacyRollWindow);

        long sentAt = 0;
        long indexedAt = 10 * 60_000L;
        long dueAt = 30 * DAY;
        List<Long> rolls = stripeRollTimes(indexedAt, dueAt, maxDelay, tick, period);
        long maxAge = maxPointerAge(sentAt, dueAt, rolls);

        check(rolls.size() == 14,
            "30d record indexed just after t=0 should roll at days 2..28 (14 times)");
        check(maxAge < retention,
            "periodic stripes must refresh the pointer before 7d retention");

        System.out.printf(Locale.ROOT,
            "STRIPE_FIXED: delay30d rolls=%d, first=%.2fd, last=%.2fd, maxPointerAge=%.2fh, retention=%.2fh%n",
            rolls.size(), rolls.get(0) / (double) DAY,
            rolls.get(rolls.size() - 1) / (double) DAY,
            maxAge / (double) HOUR, retention / (double) HOUR);
    }

    static void randomizedSafety(long retentionHours, int samples, long seed) {
        Random r = new Random(seed);
        long maxDelay = 30 * DAY;
        long tick = HOUR;
        long legacyRollWindow = 2 * DAY;
        long retention = retentionHours * HOUR;
        long period = safePeriod(retention, legacyRollWindow);

        long worstAge = 0;
        int maxRolls = 0;
        long totalRolls = 0;

        for (int i = 0; i < samples; i++) {
            long sentAt = Math.floorMod(r.nextLong(), 10 * DAY);
            long delay = 1 + Math.floorMod(r.nextLong(), maxDelay);
            long indexLagBound = Math.min(HOUR - 1, Math.max(0, delay - 1));
            long indexLag = indexLagBound == 0 ? 0 : Math.floorMod(r.nextLong(), indexLagBound + 1);
            long indexedAt = sentAt + indexLag;
            long dueAt = sentAt + delay;

            List<Long> rolls = stripeRollTimes(indexedAt, dueAt, maxDelay, tick, period);
            long age = maxPointerAge(sentAt, dueAt, rolls);
            worstAge = Math.max(worstAge, age);
            maxRolls = Math.max(maxRolls, rolls.size());
            totalRolls += rolls.size();

            check(age < retention,
                "stripe safety violated: retention=" + retentionHours + "h age=" + (age / (double) HOUR));
        }

        System.out.printf(Locale.ROOT,
            "STRIPE_RANDOM: retention=%dh period=%.2fh samples=%d worstPointerAge=%.2fh avgRolls=%.2f maxRolls=%d%n",
            retentionHours, period / (double) HOUR, samples,
            worstAge / (double) HOUR, totalRolls / (double) samples, maxRolls);
    }

    static void restartCatchupInvariant() {
        long persisted = 100 * HOUR;
        long restartAt = 110 * HOUR;
        long tick = HOUR;
        List<Long> replayed = new ArrayList<>();
        for (long t = persisted + tick; t <= restartAt; t += tick) replayed.add(t);
        check(replayed.size() == 10, "must replay every missed scheduler tick");
        check(replayed.get(0) == 101 * HOUR && replayed.get(9) == 110 * HOUR,
            "restart catch-up tick ordering");
        System.out.println("RESTART: scheduler-time checkpoint replays 10 missed hourly ticks in order");
    }

    static void failedRollInvariant() {
        long retention = 48 * HOUR;
        long tick = HOUR;
        long period = safePeriod(retention, 2 * DAY);
        long worstTwoCycleGap = 2 * period + tick;
        check(worstTwoCycleGap > retention,
            "with a skipped refresh, advancing checkpoint can exceed retention budget");
        System.out.printf(Locale.ROOT,
            "FAILURE: retention=%.2fh period=%.2fh; one skipped refresh can create ~%.2fh gap -> checkpoint must not advance on retryable failure%n",
            retention / (double) HOUR, period / (double) HOUR,
            worstTwoCycleGap / (double) HOUR);
    }

    public static void main(String[] args) {
        fixedCounterexamples();
        stripeFixedCases();
        randomizedSafety(48, 100_000, 20260913L);
        randomizedSafety(72, 100_000, 20260914L);
        randomizedSafety(168, 100_000, 20260915L);
        restartCatchupInvariant();
        failedRollInvariant();
        System.out.println("ALL_ASSERTIONS_PASSED");
    }
}
