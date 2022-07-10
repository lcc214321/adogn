package com.dongyulong.dogn.mq.core.consume;

import lombok.Data;

/**
 * @author zhangshaolong
 * @create 2022/1/17
 **/
@Data
public class MqConfig {

    public MqConfig(String topic, String group, String tags) {
        this.topic = topic;
        this.group = group;
        this.tags = tags;
    }

    private String topic;

    /** 同一group，消息互斥；不同group，全量消息 */
    private String group;

    /** 订阅得tags可以多个，用||分隔，默认是*(全部) */
    private String tags = "*";

    /** 实现类是否需要知道重试次数 */
    private boolean retryTimesAware = false;

    /** 消费线程-最大线程数 */
    private Integer consumeThreadMax = 10;

    /** 消费线程-最小线程数 */
    private Integer consumeThreadMin = 5;

    private Integer consumeConcurrentlyMaxSpan = 1000;

    /**
     * 消费模式
     */
    private boolean isBroadcast = false;

    /**
     * 每次拉去的数量
     */
    private Integer pullBatchSize;

    /**
     * 每次拉去的数量
     */
    private Integer maxReconsumeTimes;

}
