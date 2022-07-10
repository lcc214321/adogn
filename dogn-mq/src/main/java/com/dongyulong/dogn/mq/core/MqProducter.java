package com.dongyulong.dogn.mq.core;

import com.dongyulong.dogn.core.log.LogHelper;
import com.dongyulong.dogn.core.tx.ITxCommittedCallback;
import com.dongyulong.dogn.core.tx.TransactionExtHelper;
import com.dongyulong.dogn.metrics.spring.InterfaceMonitor;
import com.dongyulong.dogn.mq.helper.RocketMQProducerHelper;
import com.dongyulong.dogn.mq.helper.RocketMQProducerHelper.EnvEnum;
import com.dongyulong.dogn.tools.json.JsonTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangshaolong
 * @create 2021/12/29
 **/
public class MqProducter {

    private static final Logger logger = LoggerFactory.getLogger(MqProducter.class);

    /**
     * 发送mq 默认mq类型hook
     *
     * @param rowKey 主键
     * @param t
     * @param topic
     */
    public static <T> void sendToMQ(String rowKey, T t, String topic) {
        sendToMQ(rowKey, t, topic, "");
    }

    /**
     * 发送数据到mq 默认mq类型hook
     *
     * @param rowKey 主键
     * @param t
     * @param topic
     * @param tags
     * @param <T>
     */
    public static <T> void sendToMQ(String rowKey, T t, String topic, final String tags) {
        sendToMQByte(rowKey, JsonTools.toJSON(t).getBytes(), topic, tags, RocketMQProducerHelper.EnvEnum.DEFAULT.getValue(), null, false, true, false);
    }

    /**
     * 发送数据到mq 默认mq类型hook
     *
     * @param rowKey 主键
     * @param t
     * @param topic
     * @param tags
     * @param <T>
     */
    public static <T> void sendToMQ(String rowKey, T t, String topic, final String tags, Integer mqType) {
        sendToMQByte(rowKey, JsonTools.toJSON(t).getBytes(), topic, tags, mqType, null, false, true, false);
    }

    /**
     * 发送数据到mq 默认mq类型hook
     *
     * @param rowKey 主键
     * @param t
     * @param topic
     * @param tags
     * @param <T>
     */
    public static <T> void sendToMQ(String rowKey, T t, String topic, final String tags, Integer mqType, boolean asyn, boolean afterCommit, boolean alarm) {
        sendToMQByte(rowKey, JsonTools.toJSON(t).getBytes(), topic, tags, mqType, null, asyn, afterCommit, alarm);
    }

    /**
     * @param rowKey     消息key
     * @param t          消息内容
     * @param topic      消息主题
     * @param tags       消息tag
     * @param delayLevel 延迟级别 [1,28] mq集群可配置，默认从1到28级的延迟时间为
     *                   1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h 3h 4h 5h 6h 7h 8h 9h 10h 11h 12h
     * @param <T>
     */
    public static <T> void sendDelayMQ(String rowKey, T t, String topic, final String tags, int delayLevel) {
        sendToMQByte(rowKey, JsonTools.toJSON(t).getBytes(), topic, tags, EnvEnum.DEFAULT.getValue(), delayLevel, false, true, false);
    }

    /**
     * @param rowKey     消息key
     * @param t          消息内容
     * @param topic      消息主题
     * @param tags       消息tag
     * @param delayLevel 延迟级别 [1,28] mq集群可配置，默认从1到28级的延迟时间为
     *                   1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h 3h 4h 5h 6h 7h 8h 9h 10h 11h 12h
     * @param <T>
     */
    public static <T> void sendDelayMQ(String rowKey, T t, String topic, final String tags, int delayLevel, boolean asyn) {
        sendToMQByte(rowKey, JsonTools.toJSON(t).getBytes(), topic, tags, EnvEnum.DEFAULT.getValue(), delayLevel, asyn, true, false);
    }


    /**
     * 发送数据byte格式
     *
     * @param rowKey
     * @param t
     * @param topic
     */
    public static void sendToMQByte(String rowKey, byte[] t, String topic) {
        sendToMQByte(rowKey, t, topic, "");
    }

    /**
     * 发送数据byte格式 带tag
     *
     * @param rowKey
     * @param t
     * @param topic
     * @param tags
     */
    public static void sendToMQByte(String rowKey, byte[] t, String topic, String tags) {
        sendToMQByte(rowKey, t, topic, tags, EnvEnum.DEFAULT.getValue());
    }

    /**
     * 发送数据byte格式 带tag
     *
     * @param rowKey
     * @param t
     * @param topic
     * @param tags
     */
    public static void sendToMQByte(String rowKey, byte[] t, String topic, String tags, Integer mqType) {
        sendToMQByte(rowKey, t, topic, tags, mqType, null, false, true, false);
    }

    /**
     * 发送数据byte格式 带tag,环境
     *
     * @param rowKey
     * @param t
     * @param topic
     * @param tags
     * @param mqType
     */
    public static void sendToMQByte(String rowKey, byte[] t, String topic, String tags, Integer mqType, Integer delayLevel, boolean asyn, boolean afterCommit, boolean alarm) {
        if (StringUtils.isNotEmpty(topic) && t != null) {
            Message msg = new Message();
            msg.setTopic(topic);
            msg.setKeys(rowKey);
            if (StringUtils.isNotEmpty(tags)) {
                msg.setTags(tags);
            }
            msg.setBody(t);
            //延迟配置使用
            if (delayLevel != null && delayLevel >= 0) {
                msg.setDelayTimeLevel(delayLevel);
            }
            sendMessage(msg, mqType, asyn, afterCommit, alarm);
        }
        logger.debug("Message send: body: {}", new String(t));
    }

    private static void sendMessage(final Message msg, final Integer mqType, final Boolean asyn, final Boolean afterCommit, final Boolean alarm) {
        long start = System.currentTimeMillis();
        ITxCommittedCallback callback = () -> {
            long start1 = System.currentTimeMillis();
            try {
                InterfaceMonitor.getInstance().addTotal("MqProducter.sendMessage.mqType:" + mqType, InterfaceMonitor.TYPE_ROCKETMQ);
                if (asyn) {
                    MqProductHelper.getInstance(mqType).aysnSend(msg, result -> {
                        if (!result) {
                            MqProductHelper.getInstance(mqType).getMqFailer().addFailMessage(msg, mqType, alarm);
                            InterfaceMonitor.getInstance().addFail("MqProducter.sendMessage.mqType:" + mqType, InterfaceMonitor.TYPE_ROCKETMQ);
                        }
                    });
                } else {
                    boolean result = MqProductHelper.getInstance(mqType).send(msg);
                    if (!result) {
                        MqProductHelper.getInstance(mqType).getMqFailer().addFailMessage(msg, mqType, alarm);
                        InterfaceMonitor.getInstance().addFail("MqProducter.sendMessage.mqType:" + mqType, InterfaceMonitor.TYPE_ROCKETMQ);
                    }
                }
            } catch (MQClientException e) {
                logger.error("MqProducter.sendMessage can't send mq! Message:{}", msg != null ? msg.toString() : null, e);
                try {
                    InterfaceMonitor.getInstance().addFail("MqProducter.sendMessage.mqType:" + mqType, InterfaceMonitor.TYPE_ROCKETMQ);
                } catch (Exception e1) {
                    logger.error("InterfaceMonitor.getInstance().addFail failed.", e1);
                }
            }
            LogHelper.logSlow(start1, 50, "sendMessage do afterCommitted");
            if (System.currentTimeMillis() - start1 > 50) {
                InterfaceMonitor.getInstance().addSlow("MqProducter.sendMessage.mqType:" + mqType, InterfaceMonitor.TYPE_ROCKETMQ);
            }
        };
        LogHelper.logSlow(start, 50, "sendMessage new ITxCommittedCallback");
        start = System.currentTimeMillis();
        //下单后，往mq中发消息，订阅方会增加后续的业务的处理
        if (TransactionExtHelper.isTransactionSynchronizationActive() && afterCommit) {
            logger.debug("TransactionExtHelper.addCommittedCallback");
            TransactionExtHelper.addCommittedCallback(callback);
            LogHelper.logSlow(start, 50, "sendMessage addCommittedCallback");
        } else {
            callback.afterCommitted();
            LogHelper.logSlow(start, 50, "sendMessage callback.afterCommitted");
        }

    }
}
