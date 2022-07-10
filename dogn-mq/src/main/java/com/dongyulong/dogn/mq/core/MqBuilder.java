package com.dongyulong.dogn.mq.core;

import com.dongyulong.dogn.mq.enums.MQTopicEnum;
import com.dongyulong.dogn.mq.enums.MessageTypeEnum;
import com.dongyulong.dogn.mq.helper.RocketMQProducerHelper;
import com.dongyulong.dogn.mq.message.TradeMqMessage;
import com.dongyulong.dogn.mq.message.messagecontext.BaseContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * 发送mq消息
 *
 * @author zhangshaolong
 * @create 2021/12/29
 **/
public class MqBuilder<T extends BaseContext> {

    /**
     * 发送到哪里
     */
    private String topic;

    /**
     * 发送到哪个tag
     */
    private String tag;

    /**
     * 是否异步
     */
    private boolean asyn;

    /**
     * 发送的消息类型
     */
    private Integer mqType = RocketMQProducerHelper.EnvEnum.DEFAULT.getValue();

    /**
     * 延迟级别
     */
    private Integer delayLevel;

    /**
     * 发送的消息类型
     */
    private MessageTypeEnum messageTypeEnum;

    /**
     * 是否在事务提交之后发送
     */
    private boolean afterCommit = true;

    /**
     * 是否报警,默认不发送报警信息
     */
    private boolean alarm = false;


    private MqBuilder(MQTopicEnum topicEnum) {
        this.topic = topicEnum.name();
    }

    public static <T extends BaseContext> MqBuilder<T> builder(MQTopicEnum topicEnum) {
        return new MqBuilder<>(topicEnum);
    }

    public MqBuilder<T> tag(String tag) {
        this.tag = Optional.of(tag).get();
        return this;
    }

    public MqBuilder<T> mqType(Integer mqType) {
        RocketMQProducerHelper.EnvEnum envEnum = RocketMQProducerHelper.EnvEnum.valueOf(mqType);
        if (null != envEnum) {
            this.mqType = mqType;
        }
        return this;
    }

    public MqBuilder<T> delayLevel(int delayLevel) {
        this.delayLevel = delayLevel;
        return this;
    }

    public MqBuilder<T> asyn(boolean asyn) {
        this.asyn = asyn;
        return this;
    }

    public MqBuilder<T> afterCommit(boolean afterCommit) {
        this.afterCommit = afterCommit;
        return this;
    }

    public MqBuilder<T> alarm(boolean alarm) {
        this.alarm = alarm;
        return this;
    }

    public MqBuilder<T> messageType(MessageTypeEnum messageTypeEnum) {
        this.messageTypeEnum = Optional.of(messageTypeEnum).get();
        return this;
    }

    /**
     * 发送固定格式的消息
     *
     * @param data
     */
    public void send(T data) {
        //没有类型不可以
        if (StringUtils.isEmpty(topic) || data == null
                || messageTypeEnum == null) {
            return;
        }
        TradeMqMessage<T> tradeMqMessage = MessageFactory.buildTradeMessage(data);
        tradeMqMessage.setMsgType(messageTypeEnum.getMessageType());
        if (delayLevel != null) {
            MqProducter.sendDelayMQ(data.getTradeNo(), tradeMqMessage, this.topic, this.tag, delayLevel, asyn);
        } else {
            MqProducter.sendToMQ(data.getTradeNo(), tradeMqMessage, this.topic, this.tag, mqType, asyn, this.afterCommit, this.alarm);
        }
    }


    /**
     * 发送固定格式的消息
     *
     * @param data
     */
    public void sendToMq(T data) {
        //没有类型不可以
        if (StringUtils.isEmpty(topic) || data == null) {
            return;
        }
        if (delayLevel != null) {
            MqProducter.sendDelayMQ(data.getTradeNo(), data, this.topic, this.tag, delayLevel, asyn);
        } else {
            MqProducter.sendToMQ(data.getTradeNo(), data, this.topic, this.tag, mqType, asyn, this.afterCommit, this.alarm);
        }
    }
}
