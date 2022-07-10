package com.dongyulong.dogn.table.id;

import com.dongyulong.dogn.table.common.IEnvNo;
import com.dongyulong.dogn.table.enums.IdLongConfig;
import com.dongyulong.dogn.tools.system.TimeUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * id最终默认排列
 * 时间戳（秒级）+部署环境2位+表6位+表后缀8位+序列号10位+机器节点4位
 *
 * @author 常江涛
 * @author dongyulong
 * @date 2020/08/04
 */
@Slf4j
public class IdWorker {

    /**
     * 机器节点id   0-15
     */
    private final long machineNodeId;
    /**
     * 数据库表id 0-63
     */
    private final long tableId;
    /**
     * 数据库分表后缀 0-255，0表示没有后缀
     */
    private final long tableSuffix;
    /**
     * 部署环境id 0-3
     */
    private final long deployEnvId;
    /**
     * 0-1023
     */
    private long sequence = 0;
    /**
     * 上次时间戳，初始值为负数
     */
    private long lastTimestamp = -1L;

    /**
     * @param machineNodeId 部署节点id
     * @param tableId       表id
     * @param tableSuffix   表后缀
     * @param deployEnvEnum 部署环境
     */
    public IdWorker(long machineNodeId, long tableId, long tableSuffix, IEnvNo deployEnvEnum) {
        this.machineNodeId = machineNodeId;
        this.tableId = tableId;
        this.tableSuffix = tableSuffix;
        this.deployEnvId = deployEnvEnum.getNo();
    }

    //-------------基础配置参数 start------------

    private static final long START_TIMESTAMP = IdLongConfig.START_TIMESTAMP.getProperty();
    private static final long MACHINE_NODE_ID_BITS = IdLongConfig.MACHINE_NODE_ID_BITS.getProperty();
    private static final long SEQUENCE_BITS = IdLongConfig.SEQUENCE_BITS.getProperty();
    private static final long TABLE_SUFFIX_BITS = IdLongConfig.TABLE_SUFFIX_BITS.getProperty();
    private static final long TABLE_ID_BITS = IdLongConfig.TABLE_NO_BITS.getProperty();
    private static final long DEPLOY_ENV_ID_BITS = IdLongConfig.DEPLOY_ENV_ID_BITS.getProperty();


    /**
     * 序列号最大值
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    private static final long SEQUENCE_SHIFT_BITS = MACHINE_NODE_ID_BITS;
    private static final long TABLE_SUFFIX_SHIFT_BITS = SEQUENCE_SHIFT_BITS + SEQUENCE_BITS;
    private static final long TABLE_ID_SHIFT_BITS = TABLE_SUFFIX_SHIFT_BITS + TABLE_SUFFIX_BITS;
    private static final long DEPLOY_ENV_ID_SHIFT_BITS = TABLE_ID_SHIFT_BITS + TABLE_ID_BITS;
    private static final long TIMESTAMP_SHIFT_BITS = DEPLOY_ENV_ID_SHIFT_BITS + DEPLOY_ENV_ID_BITS;


    /**
     * 下一个ID生成算法
     *
     * @return -
     */
    public synchronized long nextId() {
        long timestamp = TimeUtils.toSystemSeconds();

        //获取当前时间戳如果小于上次时间戳，则表示时间戳获取出现异常
        if (timestamp < lastTimestamp) {
            log.error("clock is moving backwards.  Rejecting requests until {}", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }
        //获取当前时间戳如果等于上次时间戳（同一毫秒内），则在序列号加一；否则序列号赋值为0，从0开始。
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = toNextTimestamp(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        //将上次时间戳值刷新
        lastTimestamp = timestamp;

        //返回结果：
        //(timestamp - twepoch) << timestampLeftShift) 表示将时间戳减去初始时间戳，再左移相应位数
        //(datacenterId << datacenterIdShift) 表示将数据id左移相应位数
        //(workerId << workerIdShift) 表示将工作id左移相应位数
        //| 是按位或运算符，例如：x | y，只有当x，y都为0的时候结果才为0，其它情况结果都为1。
        //因为个部分只有相应位上的值有意义，其它位上都是0，所以将各部分的值进行 | 运算就能得到最终拼接好的id
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT_BITS) |
                (deployEnvId << DEPLOY_ENV_ID_SHIFT_BITS) |
                (tableId << TABLE_ID_SHIFT_BITS) |
                (tableSuffix << TABLE_SUFFIX_SHIFT_BITS) |
                (sequence << SEQUENCE_SHIFT_BITS) |
                machineNodeId;
    }

    /**
     * 解析表编号
     *
     * @param id 生成的Id
     * @return 表编号
     */
    public static long getTableNo(long id) {
        return id >> TABLE_ID_SHIFT_BITS & (2L << TABLE_ID_BITS - 1);
    }

    /**
     * 解析表后缀
     *
     * @param id 生成的id
     * @return 表后缀
     */
    public static long getTableSuffix(long id) {
        return id >> TABLE_SUFFIX_SHIFT_BITS & (2L << TABLE_SUFFIX_BITS - 1);
    }

    /**
     * 获取时间戳，并与上次时间戳比较
     *
     * @param lastTimestamp 当前记录最新时间戳
     * @return 返回比lastTimestamp大的时间戳
     */
    private long toNextTimestamp(long lastTimestamp) {
        long timestamp = TimeUtils.toSystemSeconds();
        while (timestamp <= lastTimestamp) {
            timestamp = TimeUtils.toSystemSeconds();
        }
        return timestamp;
    }


}
