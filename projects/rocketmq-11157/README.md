# RocketMQ #11157 independent verification

Status: **G1/G2 complete enough to justify upstream-level regression testing; G3 design prototype only.**

Target:
- Issue: apache/rocketmq#11156
- PR: apache/rocketmq#11157
- PR head verified during this investigation: `b3b74908ce56a0c3f2eee8b30d632895002fdc1d`

## Confirmed facts

1. `TimerRocksDBRecord` is keyed by `delayTime + uniqKey`, while the value stores only `sizePy + offsetPy`; the message body remains in CommitLog.
2. A rolled message is written back to `TIMER_TOPIC`; when it is indexed again, the existing RocksDB record is updated with the refreshed CommitLog offset/size.
3. The original RIP-75 timer implementation had explicit recurring roll semantics. The current file timer also schedules intermediate roll times for messages beyond the timer roll window.
4. The merged RIP-82 Timeline implementation intentionally scanned a broad future range periodically, causing recurring roll of far-future records.
5. PR #11157 changes this to a monotonic one-pass cursor over final due time and explicitly tests that a long-delay message is rolled at most once.
6. RocketMQ defaults include `timerMaxDelaySec = 72h` and `timerRocksDBRollRangeHours = 2h` on the PR base. The shipped `distribution/conf/broker.conf` sets `fileReservedTime = 48h`.

## Counterexample for PR #11157

Let `M = timerMaxDelay`, `W = roll range`, and scheduler time be `t`.

The PR scans approximately:

```text
[t + M - W, t + M)
```

and never revisits that final-due-time interval.

With the current defaults:

```text
M = 72h
W = 2h
cursor ~= now + 70h
```

Therefore a newly created timer whose delay is less than 70h is already behind the roll cursor when it is indexed. Example:

```text
sent at t=0
due at t=60h
fileReservedTime=48h
```

The record is never rolled by the PR model. Its RocksDB value still points to the original CommitLog record, which can exceed the configured 48h retention before the 60h due time.

A max-delay message has a related failure mode: a 72h timer is rolled approximately once near t=0, leaving the refreshed CommitLog pointer about 70h old by final delivery.

There is also a late-index race: if `[70h,72h)` is scanned and a 71h record is inserted afterward, the monotonic cursor never revisits the interval.

## Independent executable model

`RocketMQ11157RollLifecycleProof.java` models:
- the PR one-pass final-due cursor;
- fixed counterexamples;
- a periodic narrow-stripe alternative;
- restart catch-up;
- checkpoint safety when a roll fails.

The model was compiled and executed locally with `javac`/`java`. The captured output is in `RocketMQ11157RollLifecycleProof.out`.

Observed fixed counterexamples:

```text
PR_FIXED: maxDelay72h rolls=1, pointerAgeAtDue=70.00h; delay60h rolls=0; late71h rolls=0
```

## Candidate design — not yet a production patch

Keep RocksDB keyed by final due time, but checkpoint **scheduler time**, not final-due time.

At each scheduler tick `T` of width `W`, scan narrow due-time stripes around:

```text
T + P
T + 2P
T + 3P
...
```

where `P` is a safe refresh period smaller than CommitLog retention. This makes long timers eligible for recurring refresh without rescanning the entire `[now, now + maxDelay]` horizon every tick.

The standalone model used a conservative prototype policy:

```text
P = min(legacy timer roll window, fileReservedTime / 2)
```

This is a **modeling choice**, not yet a proposed upstream default.

Randomized tests (100k samples per retention setting) passed the normal-path pointer-age invariant, but production validation is still required.

## Additional correctness requirement

PR #11157 persists the roll checkpoint after waiting for all reput tasks, but task completion does not mean successful CommitLog refresh: retry exhaustion, message read failure, or conversion failure can still count down the latch. A retryable roll failure must not be treated as successful checkpoint progress; otherwise a refresh opportunity can be skipped permanently.

## Remaining gate

Before making any upstream review claim, run a real RocketMQ regression test on PR #11157 that demonstrates at least one of:

1. a newly indexed 60h timer is behind the one-pass roll cursor and receives no roll;
2. after forcing the original CommitLog mapped file past retention, the RocksDB timer record still exists but its `offsetPy/sizePy` no longer resolves to a message before final due;
3. a retryable roll failure does not advance durable roll progress.

No Apache issue/PR comment has been posted from this investigation.
