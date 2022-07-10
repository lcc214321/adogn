package com.dongyulong.dogn.mq.core.consume.listener;

import com.dongyulong.dogn.mq.core.consume.MqConfig;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;

/**
 * @author zhangshaolong
 * @create 2022/1/20
 **/
public class Label {

    /**
     * 埋点信息
     **/
    private static final Gauge consumeLatency = Gauge.build().name("consume_latency_milseconds_new")
            .labelNames("group", "topic", "tag", "result").help("Consume latency in seconds.").register();

    private static final Counter mqCounter = Counter.build().name("mq_msg_count_new").help("MQ message count.")
            .labelNames("group", "topic", "tag", "result").register();


    public enum LabelEnum {
        /**
         * 成功
         **/
        SUCCESS("success"),
        /**
         * 不在重试
         **/
        DROP("drop"),
        /**
         * 重试
         **/
        RETRY("retry"),
        /**
         * 处理失败重试
         **/
        FAIL("fail");

        private String lable;

        LabelEnum(String lable) {
            this.lable = lable;
        }

        public void incLabel(MqConfig mqConfig, long time) {
            mqCounter.labels(mqConfig.getGroup(), mqConfig.getTopic(), mqConfig.getTags(), lable).inc();
            consumeLatency.labels(mqConfig.getGroup(), mqConfig.getTopic(), mqConfig.getTags(), lable).inc(time);
        }
    }


}
