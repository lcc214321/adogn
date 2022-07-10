package com.dongyulong.dogn.mq.common;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/97:26 下午
 * @since v1.0
 */
public class ProducerHelper implements InitializingBean {
    private static final DefaultMQProducer producer = new DefaultMQProducer("C_DEFAULT_PRODUCER");
    private static Logger logger = LoggerFactory.getLogger(ProducerHelper.class);
    private String namesrvAddr;
    private static LinkedBlockingDeque<Message> retryQueue = new LinkedBlockingDeque<>();

    @Override
    public void afterPropertiesSet() {
        Preconditions.checkArgument(StringUtils.isNotEmpty(namesrvAddr), "namesrvAddr should not be null!");
        logger.info("RocketMQ ProducuerHelp Start Deamon.");
        producer.setNamesrvAddr(namesrvAddr);
        producer.setInstanceName("Producer");
        try {
            logger.info("ProducerHelper started!");
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * send msg to rocket mq
     *
     * @param msg
     * @throws InterruptedException
     * @throws RemotingException
     * @throws MQClientException
     * @throws MQBrokerException
     */
    public static void send(Message msg) throws InterruptedException, RemotingException,
            MQClientException, MQBrokerException {
        producer.send(msg);
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }
}
