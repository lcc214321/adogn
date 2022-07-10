package com.dongyulong.dogn.mq.common;

import cn.hutool.core.util.TypeUtil;
import com.dongyulong.dogn.mq.entities.RocketMQConsumerConfig;
import com.dongyulong.dogn.tools.json.JsonMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import javax.annotation.PreDestroy;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/97:21 下午
 * @since v1.0
 */
public abstract class AbstractRocketMQSubscriber<T> implements IRocketMQSubscriber<T> {

    private static Logger logger = LoggerFactory.getLogger(AbstractRocketMQSubscriber.class);
    private static Logger MQMessageLogger = LoggerFactory.getLogger("MQMessageLogger");
    private static Logger slowLogger = LoggerFactory.getLogger("SlowLogger");
    DefaultMQPushConsumer consumer;

    private static Logger rejectLogger = LoggerFactory.getLogger("MQRejectLogger");
    private ConfigurableListableBeanFactory factory = null;
    private static final Gauge consumeLatency = Gauge.build().name("consume_latency_milseconds")
            .labelNames("group", "topic", "tag", "result").help("Consume latency in seconds.").register();
    private static final Counter mqCounter = Counter.build().name("mq_msg_count").help("MQ message count.")
            .labelNames("group", "topic", "tag", "result").register();

    private static String DEFAULT_LANE = "default";
    private static String SIGN = "%";

    /**
     * topic
     */
    private String topic;

    /**
     * 同一group，消息互斥；不同group，全量消息
     */
    private String group;

    /**
     * 订阅得tags可以多个，用||分隔，默认是*(全部)
     */
    private String tags = "*";

    /**
     * 实现类是否需要知道重试次数
     */
    private boolean retryTimesAware = false;

    /**
     * 消费线程-最大线程数
     */
    private Integer consumeThreadMax;

    /**
     * 消费线程-最小线程数
     */
    private Integer consumeThreadMin;

    private Integer consumeConcurrentlyMaxSpan = 1000;

    private boolean isBroadcast = false;

    /**
     * 只读
     */
//    private boolean readOnly = true;

//    private String txBeanName;
    @Override
    public void start() throws MQClientException {
        init();
        //params check
        Preconditions.checkNotNull(topic, "topic不能为空！");
        Preconditions.checkNotNull(group, "group不能为空！");
        Preconditions.checkNotNull(tags, "tags不能为空！");
        String lane = getLane();
        if (!(StringUtils.isEmpty(lane) || DEFAULT_LANE.equals(lane))) {
            setTopic(getTopic().concat(SIGN).concat(lane));
            setGroup(getGroup().concat(SIGN).concat(lane));
        }
        reciveMsgFromMQ();
    }

    @Override
    public void execute(T t) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void execute(T t, int reconsumeTimes) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reciveMsgFromMQ() throws MQClientException {
        Preconditions.checkNotNull(RocketMQConsumerConfig.nameSrvAddr, "NameServer Address should not be null!");
        //同group消息互斥，不同group全量消息
        consumer = new DefaultMQPushConsumer(getGroup());
//        try {
        consumer.setNamesrvAddr(RocketMQConsumerConfig.nameSrvAddr);
        consumer.subscribe(getTopic(), getTags());
//        } catch (MQClientException e) {
//            logger.error("{}.reciveMsgFromMQ faild.", this.getClass().getSimpleName(), e);
//        }
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        if (consumeThreadMax != null) {
            Preconditions.checkArgument(consumeThreadMax > 0 && consumeThreadMax >= (consumeThreadMin == null ? 1 :
                    consumeThreadMin), "consumeThreadMax must be larger than 0 and consumeThreadMin!");
            consumer.setConsumeThreadMax(consumeThreadMax);
        }
        if (consumeThreadMin != null) {
            Preconditions.checkArgument(consumeThreadMin > 0, "consumeThreadMax must be larger than 0!");
            consumer.setConsumeThreadMin(consumeThreadMin);
        }
        Preconditions.checkArgument(consumeConcurrentlyMaxSpan >= 100 && consumeConcurrentlyMaxSpan <= 5000, "consumeConcurrentlyMaxSpan must be between 100 and 5000");
        consumer.setConsumeConcurrentlyMaxSpan(consumeConcurrentlyMaxSpan);
        consumer.setMessageModel(isBroadcast ? MessageModel.BROADCASTING : MessageModel.CLUSTERING);
        consumer.setConsumeTimeout(RocketMQConsumerConfig.consumeTimeoutMins);

        consumer.registerMessageListener(new MessageListenerConcurrently() {

            private String simpleName = AbstractRocketMQSubscriber.this.getClass().getSimpleName();
            private Type type = TypeUtil.getTypeArgument(AbstractRocketMQSubscriber.this.getClass());
            //            private Type type = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) AbstractRocketMQSubscriber.this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            private IRocketMQSubscriber proxy = SpringConext.getApplicationContext().getBean(AbstractRocketMQSubscriber.this.getClass());

            @SuppressWarnings({"unchecked"})
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                //默认消费成功
                ConsumeConcurrentlyStatus status = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                String tag = tags;
                for (final MessageExt messageExt : msgs) {
                    if (messageExt == null) {
                        logger.error("{}.reciveMsgFromMQ messageExt is NULL. continue!", simpleName);
                        continue;
                    }
                    Stopwatch sw = Stopwatch.createStarted();
                    try {
                        if (null != messageExt.getBody()) {
                            tag = StringUtils.isEmpty(messageExt.getTags()) ? tags : messageExt.getTags();
                            if (messageExt.getProperties().get("MAX_OFFSET") != null) {
                                long offset = Long.parseLong(messageExt.getProperties().get("MAX_OFFSET")) - messageExt.getQueueOffset();
                                if (offset > RocketMQConsumerConfig.maxOffsetThreshold) {
                                    logger.warn("{}消息积压过多,offset:{},超过阈值{},请确认是否可以重置消费点或调大阈值！", getGroup(), offset, RocketMQConsumerConfig.maxOffsetThreshold);
                                    shutdown();
                                    System.exit(1);
                                }
                            }

                            final String json = new String(messageExt.getBody());
                            Object jsonObject = String.class.equals(type) ? json : JsonMapper.json2Bean(json, type);

                            MQMessageLogger.info("{}.reciveMsgFromMQ message topic:{}, tags:{}, body:{}", simpleName, messageExt.getTopic(), messageExt.getTags(), json);
                            if (retryTimesAware) {
                                proxy.execute((T) jsonObject, messageExt.getReconsumeTimes());
                            } else {
                                proxy.execute((T) jsonObject);
                            }
                        }
                        if (messageExt.getReconsumeTimes() > 0) {
                            rejectLogger.info("{}.{} consumed after retry {} times! msgBody:{}", simpleName, messageExt.getTopic(), messageExt.getReconsumeTimes(), new String(messageExt.getBody(), Charsets.UTF_8));
                        }
                        mqCounter.labels(group, topic, tag, "success").inc();
                        sw.stop();
                        if (sw.elapsed(TimeUnit.MILLISECONDS) > 500) {
                            slowLogger.info("{}.execute (cost {} ms)------------", simpleName, sw.elapsed(TimeUnit.MILLISECONDS));
                        }
                        consumeLatency.labels(group, topic, tag, "success").inc(sw.elapsed(TimeUnit.MILLISECONDS));
                    } catch (IllegalArgumentException e) {
                        //如果参数错误 也从队列中删除 打印日志
                        logger.error("{}.reciveMsgFromMQ consumeMessage IllegalArgumentException faild. messageExt:{}", simpleName, messageExt, e);
                        mqCounter.labels(group, topic, tag, "success").inc();
                        sw.stop();
                        consumeLatency.labels(group, topic, tag, "success").inc(sw.elapsed(TimeUnit.MILLISECONDS));
                    } catch (Throwable e) {
                        logger.error("{}.reciveMsgFromMQ consumeMessage faild. messageExt:{}", simpleName, messageExt, e);
                        int retryCount = messageExt.getReconsumeTimes();
                        if (retryCount > RocketMQConsumerConfig.retryThreshold) {
                            rejectLogger.error("{}.{} consume failed after retry {} times! rejected! msgBody:{}", simpleName, messageExt.getTopic(), retryCount, new String(messageExt.getBody(), Charsets.UTF_8));
                            mqCounter.labels(group, topic, tag, "drop").inc();
                            sw.stop();
                            consumeLatency.labels(group, topic, tag, "drop").inc(sw.elapsed(TimeUnit.MILLISECONDS));
                        } else {
                            mqCounter.labels(group, topic, tag, "fail").inc();
                            sw.stop();
                            consumeLatency.labels(group, topic, tag, "fail").inc(sw.elapsed(TimeUnit.MILLISECONDS));
                            logger.error("Msg: {} will retry!", messageExt.getMsgId());
                            //重试
                            status = ConsumeConcurrentlyStatus.RECONSUME_LATER;
                            break;
                        }
                    }
                }
                return status;
            }
        });
        try {
            consumer.start();
        } catch (MQClientException e) {
            logger.error("{}.reciveMsgFromMQ consumer.start faild ", this.getClass().getSimpleName(), e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                logger.info("ShutdownHook Executed...");
            }
        }));
    }

    @Override
    @PreDestroy
    public void shutdown() {
        if (consumer != null) {
            consumer.shutdown();
            logger.info("consumer {} shutDowned...", getGroup());
        }
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isRetryTimesAware() {
        return retryTimesAware;
    }

    public void setRetryTimesAware(boolean retryTimesAware) {
        this.retryTimesAware = retryTimesAware;
    }

    public Integer getConsumeThreadMax() {
        return consumeThreadMax;
    }

    public void setConsumeThreadMax(Integer consumeThreadMax) {
        this.consumeThreadMax = consumeThreadMax;
    }

    public Integer getConsumeThreadMin() {
        return consumeThreadMin;
    }

    public void setConsumeThreadMin(Integer consumeThreadMin) {
        this.consumeThreadMin = consumeThreadMin;
    }

    public Integer getConsumeConcurrentlyMaxSpan() {
        return consumeConcurrentlyMaxSpan;
    }

    public void setConsumeConcurrentlyMaxSpan(Integer consumeConcurrentlyMaxSpan) {
        this.consumeConcurrentlyMaxSpan = consumeConcurrentlyMaxSpan;
    }

    public void setBroadcast(boolean broadcast) {
        isBroadcast = broadcast;
    }

    //    public boolean isReadOnly() {
//        return readOnly;
//    }
//
//    public void setReadOnly(boolean readOnly) {
//        this.readOnly = readOnly;
//    }

    //    public String getTxBeanName() {
//        return txBeanName;
//    }
//
//    public void setTxBeanName(String txBeanName) {
//        this.txBeanName = txBeanName;
//    }
    private String getLane() {
        String lane = System.getenv(Lane.ENV_NAME);
        if (StringUtils.isEmpty(lane)) {
            lane = DEFAULT_LANE;
        }
        logger.debug("use lane is {}", lane);
        return lane;
    }
}
