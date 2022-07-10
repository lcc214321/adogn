package com.dongyulong.dogn.mq.core.consume;

import com.dongyulong.dogn.mq.entities.RocketMQConsumerConfig;
import com.google.common.base.Preconditions;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * @author zhangshaolong
 * @create 2022/1/17
 **/
public class MqClientService {

    /**
     * 启动服务信息
     *
     * @param mqConfig
     * @return
     * @throws MQClientException
     */
    public static DefaultMQPushConsumer getDefaultMQPushConsumer(MqConfig mqConfig) throws MQClientException {
        Preconditions.checkNotNull(RocketMQConsumerConfig.nameSrvAddr, "NameServer Address should not be null!");
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(mqConfig.getGroup());

        consumer.setNamesrvAddr(RocketMQConsumerConfig.nameSrvAddr);
        consumer.subscribe(mqConfig.getTopic(), mqConfig.getTags());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        if (mqConfig.getConsumeThreadMax() != null) {
            Preconditions.checkArgument(mqConfig.getConsumeThreadMax() > 0 && mqConfig.getConsumeThreadMax() >= (mqConfig.getConsumeThreadMin() == null ? 1 :
                    mqConfig.getConsumeThreadMin()), "consumeThreadMax must be larger than 0 and consumeThreadMin!");
            consumer.setConsumeThreadMax(mqConfig.getConsumeThreadMax());
        }
        if (mqConfig.getConsumeThreadMin() != null) {
            Preconditions.checkArgument(mqConfig.getConsumeThreadMin() > 0, "consumeThreadMax must be larger than 0!");
            consumer.setConsumeThreadMin(mqConfig.getConsumeThreadMin());
        }
        Preconditions.checkArgument(mqConfig.getConsumeConcurrentlyMaxSpan() >= 100 && mqConfig.getConsumeConcurrentlyMaxSpan() <= 5000, "consumeConcurrentlyMaxSpan must be between 100 and 5000");
        consumer.setConsumeConcurrentlyMaxSpan(mqConfig.getConsumeConcurrentlyMaxSpan());
        consumer.setMessageModel(mqConfig.isBroadcast() ? MessageModel.BROADCASTING : MessageModel.CLUSTERING);
        consumer.setConsumeTimeout(RocketMQConsumerConfig.consumeTimeoutMins);
        consumer.setPullBatchSize(mqConfig.getPullBatchSize());
        consumer.setMaxReconsumeTimes(mqConfig.getMaxReconsumeTimes());
        return consumer;
    }

}
