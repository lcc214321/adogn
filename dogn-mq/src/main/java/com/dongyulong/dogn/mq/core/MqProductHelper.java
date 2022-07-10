package com.dongyulong.dogn.mq.core;

import com.dongyulong.dogn.apollo.tools.PropertyUtils;
import com.dongyulong.dogn.autoconfigure.tools.SpringUtils;
import com.dongyulong.dogn.core.log.LogHelper;
import com.dongyulong.dogn.mq.common.Lane;
import com.dongyulong.dogn.mq.entities.RocketMQProducerConfig;
import com.dongyulong.dogn.mq.helper.RocketMQProducerHelper;
import com.dongyulong.dogn.mq.helper.RocketMQProducerHelper.EnvEnum;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangshaolong
 * @create 2021/12/29
 **/
public class MqProductHelper {
    private static Logger logger = LoggerFactory.getLogger(MqProductHelper.class);
    private static Map<EnvEnum, MqProductHelper> helperMap = new EnumMap<EnvEnum, MqProductHelper>(EnvEnum.class);
    private DefaultMQProducer producer = null;
    private MqFailer mqFailer;
    private int type = 0;
    private LoadingCache<String, List> qSizeCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List>() {
                @Override
                public List load(String key) throws Exception {
                    producer.getDefaultMQProducerImpl().getmQClientFactory().getMQAdminImpl().setTimeoutMillis(PropertyUtils.getProperty("fetch_publish_mq_timeout", Long.class, 100L));
                    return producer.fetchPublishMessageQueues(key);
                }
            });
    private static String DEFAULT_LANE = "DEFAULT";
    private static String SIGN = "%";


    private MqProductHelper() {
    }

    private MqProductHelper(int type) {
        this.type = type;
        init();
        mqFailer = new MqFailer(producer);
        mqFailer.start();
        addShutdownHook();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (producer != null) {
                    producer.shutdown();
                }
            }
        }));
    }

    public static MqProductHelper getInstance() {
        return getInstance(EnvEnum.DEFAULT.getValue());
    }

    /**
     * 实例化send mq对象
     *
     * @param type 1：DEFAULT, 2: ONLINE
     * @return
     */
    public static MqProductHelper getInstance(int type) {
        if (EnvEnum.valueOf(type) == null) {
            logger.error("RocketMQProducerHelper.getInstance param is miss!");
            return null;
        }
        if (helperMap.get(EnvEnum.valueOf(type)) == null) {
            synchronized (RocketMQProducerHelper.class) {
                if (helperMap.get(EnvEnum.valueOf(type)) == null) {
                    helperMap.put(EnvEnum.valueOf(type), new MqProductHelper(type));
                }
            }
        }
        return helperMap.get(EnvEnum.valueOf(type));
    }

    private void init() {
        EnvEnum en = EnvEnum.valueOf(type);
        if (en == null) {
            logger.error("DefaultProducer start failed. type is not exist type = ", type);
            producer = null;
        }
        String addr = SpringUtils.getBean(RocketMQProducerConfig.class).getNamesrvAddr(en);
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer("P_DIDA_INDEX_" + en.toString().toUpperCase());
        defaultMQProducer.setNamesrvAddr(addr);
        defaultMQProducer.setInstanceName("P_DIDA_INDEX_" + en.toString().toUpperCase());
        defaultMQProducer.setRetryAnotherBrokerWhenNotStoreOK(true);
        defaultMQProducer.setCompressMsgBodyOverHowmuch(1024 * 10);
        try {
            defaultMQProducer.start();
        } catch (Exception e) {
            logger.error("DefaultProducer start failed.", e);
            defaultMQProducer = null;
        }
        producer = defaultMQProducer;
    }

    /**
     * 同步发送mq 根据key 判断是否hash路由的规则
     *
     * @param msg
     * @return
     * @throws MQClientException
     */
    public boolean send(Message msg) throws MQClientException {
        if (StringUtils.isEmpty(msg.getTopic())) {
            throw new MQClientException(ResponseCode.TOPIC_NOT_EXIST, "the topic is null");
        }
        if (producer == null) {
            throw new MQClientException(ResponseCode.SERVICE_NOT_AVAILABLE, "can't connect service");
        }
        laneMessage(msg);
        return sendMessage(msg, null);
    }

    /**
     * 异步发送mq 根据key 判断是否hash路由的规则
     *
     * @param msg
     * @return
     * @throws MQClientException
     */
    public boolean aysnSend(Message msg, ResultCallback resultCallback) throws MQClientException {
        if (StringUtils.isEmpty(msg.getTopic())) {
            throw new MQClientException(ResponseCode.TOPIC_NOT_EXIST, "the topic is null");
        }
        if (producer == null) {
            throw new MQClientException(ResponseCode.SERVICE_NOT_AVAILABLE, "can't connect service");
        }
        laneMessage(msg);
        return sendMessage(msg, resultCallback);
    }

    /**
     * 发送处理消息
     *
     * @param msg
     */
    private boolean sendMessage(Message msg, ResultCallback resultCallback) {
        final String orderby = msg.getKeys();
        MessageQueue messageQueue = null;
        long start = System.currentTimeMillis();
        if (StringUtils.isNotEmpty(orderby)) {
            List<MessageQueue> queues = null;
            try {
                queues = qSizeCache.get(msg.getTopic());
            } catch (Exception e) {
                logger.error("RocketMQProducerHelper.qSizeCache.get({}) timeout. msgkey:{},msgbody:{}", msg.getTopic(), msg.getKeys(), new String(msg.getBody()));
                //TODO 处理失败
                return false;
            }
            LogHelper.logSlow(start, 50, "sendMessage producer.fetchPublishMessageQueues");
            int selectedQueueIndex = Math.abs(orderby.hashCode() % (queues.size()));
            logger.debug("msgKey:{} sendToQueueIndex:{}", msg.getKeys(), selectedQueueIndex);
            messageQueue = queues.get(selectedQueueIndex);
        }
        return sendMessage(msg, messageQueue, resultCallback);
    }

    /**
     * 发送消息
     *
     * @param msg
     * @param messageQueue
     */
    private boolean sendMessage(Message msg, MessageQueue messageQueue, ResultCallback resultCallback) {
        long start = System.currentTimeMillis();
        boolean result;
        try {
            if (resultCallback == null) {
                result = sendMessageNoCallback(msg, messageQueue);
            } else {
                result = sendMessageWithCallback(msg, messageQueue, resultCallback);
            }
            logger.debug("send success! NamesrvAddr:{},Message:{}", producer.getNamesrvAddr(), msg);
            return result;
        } catch (Exception e) {
            logger.error("RocketMQProducerHelper.send failed.", e);
            return false;
        } finally {
            LogHelper.logSlow(start, 50, "sendMessage producer.send");
        }
    }

    /**
     * 同步发送
     *
     * @param msg
     * @param messageQueue
     * @return
     * @throws Exception
     */
    private boolean sendMessageNoCallback(Message msg, MessageQueue messageQueue) throws Exception {
        SendResult result;
        if (messageQueue == null) {
            result = producer.send(msg);
        } else {
            result = producer.send(msg, messageQueue);
        }
        //返回的不是成功的结果信息
        return result != null && result.getSendStatus() == SendStatus.SEND_OK;
    }


    /**
     * 异步发送队列消息
     *
     * @param msg
     * @param messageQueue
     * @param resultCallback
     * @return
     * @throws Exception
     */
    private boolean sendMessageWithCallback(Message msg, MessageQueue messageQueue, ResultCallback resultCallback) throws Exception {
        if (messageQueue == null) {
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    resultCallback.callBack(true);
                }

                @Override
                public void onException(Throwable throwable) {
                    logger.error("send mq onException", throwable);
                    resultCallback.callBack(false);
                }
            });
        } else {
            producer.send(msg, messageQueue, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    resultCallback.callBack(true);
                }

                @Override
                public void onException(Throwable throwable) {
                    logger.error("send mq onException", throwable);
                    resultCallback.callBack(false);
                }
            });
        }
        return true;
    }


    @PreDestroy
    public void close() {
        if (producer != null) {
            producer.shutdown();
        }
    }

    public synchronized static void refresh() {
        for (Map.Entry<EnvEnum, MqProductHelper> entry : helperMap.entrySet()) {
            entry.getValue().close();
            entry.getValue().init();
        }
    }

    /**
     * 如果是泳道服务并且可允许的topic,topic会加上后缀 %{lane}
     *
     * @param msg
     */
    private static void laneMessage(Message msg) {
        List<String> enableLaneTopic = getLaneTopic();
        String lane = Lane.getLane();
        if (enableLaneTopic.contains(msg.getTopic()) || enableLaneTopic.contains("*")) {
            if (!(StringUtils.isEmpty(lane) || DEFAULT_LANE.equalsIgnoreCase(lane))) {
                msg.setTopic(msg.getTopic().concat(SIGN).concat(lane));
            }
        }
    }


    /**
     * @return enable topic in lane
     */
    private static List<String> getLaneTopic() {
        String enableTopic = PropertyUtils.getProperty("lane.enable.topic");
        List<String> topicList = new ArrayList<>();
        if (StringUtils.isNotEmpty(enableTopic)) {
            List<String> strings = Arrays.asList(enableTopic.split(","));
            topicList = new ArrayList<>(strings);
        }
        return topicList;
    }

    public MqFailer getMqFailer() {
        return mqFailer;
    }
}
