/*
 * Independent regression test for Apache RocketMQ PR #11157.
 * This file is copied into the upstream checkout only inside the lab CI job.
 */
package org.apache.rocketmq.store.rocksdb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.store.config.MessageStoreConfig;
import org.apache.rocketmq.store.timer.rocksdb.TimerRocksDBRecord;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.rocketmq.store.rocksdb.MessageRocksDBStorage.TIMER_COLUMN_FAMILY;

public class MessageRocksDBRollCursorRegressionTest {
    private MessageRocksDBStorage storage;
    private String storePath;

    @Before
    public void setUp() throws Exception {
        storePath = System.getProperty("java.io.tmpdir") + File.separator
            + "message_rocksdb_roll_cursor_" + System.currentTimeMillis();
        MessageStoreConfig config = new MessageStoreConfig();
        config.setStorePathRootDir(storePath);
        storage = new MessageRocksDBStorage(config);
    }

    @After
    public void tearDown() {
        if (storage != null) {
            storage.shutdown();
        }
        UtilAll.deleteFile(new File(storePath));
    }

    /**
     * PR #11157 initializes its final-due cursor at approximately
     * now + timerMaxDelay - rollRange. With the defaults that is now+70h.
     * A newly indexed 60h timer is therefore already behind the cursor.
     *
     * This test applies the PR's monotonic cursor progression to the real
     * RocksDB timer index and expresses the required lifecycle property:
     * a timer whose due time exceeds CommitLog retention must still have a
     * future roll opportunity after it is indexed.
     */
    @Test
    public void testNew60HourTimerMustHaveFutureRollOpportunity() {
        long now = 2_000_000_000_000L;
        long maxDelay = TimeUnit.HOURS.toMillis(72);
        long range = TimeUnit.HOURS.toMillis(2);
        long commitLogRetention = TimeUnit.HOURS.toMillis(48);
        long dueAt = now + TimeUnit.HOURS.toMillis(60);

        Assert.assertTrue("test requires due time beyond CommitLog retention",
            dueAt - now > commitLogRetention);

        writeTimerRecord(dueAt, "new-60h-timer");

        long checkpoint = now + maxDelay - range; // PR #11157 initialization: now+70h
        boolean seenByAnyFutureRollWindow = false;

        // Walk every future final-due window that can still be relevant to this record.
        for (int i = 0; i < 48; i++) {
            List<TimerRocksDBRecord> records = storage.scanRecordsForTimer(
                TIMER_COLUMN_FAMILY, checkpoint, checkpoint + range, 10, null);
            if (records != null) {
                for (TimerRocksDBRecord record : records) {
                    if ("new-60h-timer".equals(record.getUniqKey())) {
                        seenByAnyFutureRollWindow = true;
                    }
                }
            }
            checkpoint += range;
        }

        Assert.assertTrue(
            "A newly indexed 60h timer is behind PR #11157's initial ~70h cursor, "
                + "so the one-pass final-due scan gives it no future roll opportunity",
            seenByAnyFutureRollWindow);
    }

    private void writeTimerRecord(long delayTime, String uniqKey) {
        TimerRocksDBRecord record = new TimerRocksDBRecord(
            delayTime, uniqKey, 100L, 200, 0L, null);
        record.setActionFlag(TimerRocksDBRecord.TIMER_ROCKSDB_PUT);
        List<TimerRocksDBRecord> records = new ArrayList<>();
        records.add(record);
        storage.writeRecordsForTimer(TIMER_COLUMN_FAMILY, records);
    }
}
