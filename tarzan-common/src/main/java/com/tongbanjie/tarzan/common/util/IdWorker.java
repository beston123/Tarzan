package com.tongbanjie.tarzan.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Twitter的分布式自增ID算法
 * 改动
 * 1、［时间前缀］改为44位，使用时间支持从当前时间起278年，［数据中心Id］改为3位
 * 2、［毫秒内的计数］随机从0-256开始自增，支持256张分表
 *
 * twitter的snowflake:
 *   (a) id构成: 42位的时间前缀(第1位long的符号位，不用) + 5位的数据中心id + 5位的机器id + 12位的当前毫秒内的计数(12位不够用时强制得到新的时间前缀)
 *   (b) 对系统时间的依赖性非常强，需关闭ntp的时间同步功能。当检测到ntp时间调整后，将会拒绝分配id
 *
 * @URL https://github.com/twitter/snowflake
 * An object that generates IDs.
 * This is broken into a separate class in case
 * we ever want to support multiple worker threads
 * per process
 *
 * @author zixiao
 * @date 16/9/23
 */
public class IdWorker {

    protected static final Logger LOG = LoggerFactory.getLogger(IdWorker.class);

    // 时间起始标记点，作为基准，一般取系统的最近时间
    private static long EPOCH;

    static {
        //2016-12-12 00:00:00
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.DECEMBER, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        EPOCH = calendar.getTimeInMillis();
    }

    // 机器标识(或者进程标识)
    private long workerId;
    // 数据中心标识
    private long dataCenterId;

    // 机器标识位数
    private static final long workerIdBits = 5L;
    // 数据中心标识位数
    private static final long dataCenterIdBits = 3L;

    //机器ID最大值
    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    //数据中心ID最大值
    private static final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    //毫秒内自增位数
    private static final long sequenceBits = 12L;

    private static final long workerIdShift = sequenceBits;
    private static final long dataCenterIdShift = sequenceBits + workerIdBits;

    // 时间毫秒左移
    private static final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    //毫秒内自增最大值 4095,12位
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

    //毫秒内自增最大值
    private long sequence = 0L;

    private long lastTimestamp = -1L;

    //sequence随机值范围 0-SEQ_ROUND
    private static final int SEQ_ROUND = 256;

    public IdWorker(long workerId) {
        this(workerId, 0L);
    }

    public IdWorker(long workerId, long dataCenterId) {
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("data center Id can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        LOG.info(String.format("worker starting. timestamp left shift %d, data center id bits %d, worker id bits %d, sequence bits %d, worker Id %d", timestampLeftShift, dataCenterIdBits, workerIdBits, sequenceBits, workerId));
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            LOG.error(String.format("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp));
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            //当前毫秒内，则+1
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                //当前毫秒内计数满了，则等待下一秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //计数重置为随机数
            sequence = ThreadLocalRandom.current().nextInt(SEQ_ROUND);
        }

        lastTimestamp = timestamp;
        //ID偏移组合生成最终的ID，并返回ID
        return ((timestamp - EPOCH) << timestampLeftShift) | (dataCenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * 等待下一个毫秒的到来, 保证返回的毫秒数在参数lastTimestamp之后
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获得系统当前毫秒数
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public static long getMaxDataCenterId() {
        return maxDataCenterId;
    }

    public static long getMaxWorkerId() {
        return maxWorkerId;
    }

    public static void main(String[] args) {
        final IdWorker idWorker = new IdWorker(0);
        for(int i=0; i<10; i++){
            System.out.println(i+": "+idWorker.nextId());
        }
    }
}


