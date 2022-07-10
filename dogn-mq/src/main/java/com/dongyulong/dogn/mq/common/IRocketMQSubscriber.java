package com.dongyulong.dogn.mq.common;

import org.apache.rocketmq.client.exception.MQClientException;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/97:22 下午
 * @since v1.0
 */
public interface IRocketMQSubscriber<T> {

    /**
     * 消费者启动.
     * 调用方法
     * <pre>
     * init
     * reciveMsgFromMQ
     * </pre>
     *
     * @throws MQClientException
     */
    void start() throws MQClientException;

    /**
     * 初始化topic，tags等.
     */
    void init();

    /**
     * 接收消息，调用execute.
     *
     * @throws MQClientException
     */
    void reciveMsgFromMQ() throws MQClientException;

    /**
     * 执行获得的消息.
     * reciveMsgFromMQ的内部调用该方法
     *
     * @param t
     * @throws Exception
     */
    void execute(T t) throws Exception;

    /**
     * 执行获得的消息.
     * reciveMsgFromMQ的内部调用该方法
     *
     * @param t
     * @throws Exception
     */
    void execute(T t, int reconsumeTimes) throws Exception;

    /**
     * shutdown时候被调用
     */
    void shutdown();

}
