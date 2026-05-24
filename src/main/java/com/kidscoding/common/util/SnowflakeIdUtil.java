package com.kidscoding.common.util;

/**
 * 雪花算法 ID 生成器
 *
 * 结构（64位）：
 *   1位符号 + 41位时间戳 + 5位数据中心 + 5位机器ID + 12位序列号
 *
 *   → 每台机器每秒可生成 400万个不重复 ID
 *   → 按时间递增，B+ 树索引友好
 */
public class SnowflakeIdUtil {

    private static final long START_TIMESTAMP = 1704067200000L; // 2024-01-01 00:00:00
    private static final long DATACENTER_BITS = 5L;
    private static final long WORKER_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_BITS); // 31
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_BITS);         // 31
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);        // 4095

    private static final long WORKER_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_SHIFT = SEQUENCE_BITS + WORKER_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_BITS + DATACENTER_BITS;

    private final long datacenterId;
    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static final SnowflakeIdUtil INSTANCE = new SnowflakeIdUtil(1, 1);

    public SnowflakeIdUtil(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId 范围: 0-31");
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("workerId 范围: 0-31");
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * 生成下一个 ID（线程安全）
     */
    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨，拒绝生成 ID");
        }

        if (currentTimestamp == lastTimestamp) {
            // 同一毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 序列号溢出，等下一毫秒
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 新的一毫秒，序列号归零
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_SHIFT)
                | (workerId << WORKER_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 全局静态方法，直接调用
     */
    public static long generateId() {
        return INSTANCE.nextId();
    }
}
