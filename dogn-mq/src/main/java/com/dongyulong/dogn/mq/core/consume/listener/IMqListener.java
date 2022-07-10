package com.dongyulong.dogn.mq.core.consume.listener;

import com.dongyulong.dogn.mq.core.consume.MqConfig;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;

/**
 * 采用mq推送的模式获取消息数据
 *
 * @author zhangshaolong
 * @create 2022/1/17
 **/
public interface IMqListener<T> extends MessageListenerConcurrently {

    /**
     * 消费业务数据信息
     *
     * @param consume
     */
    boolean receive(T consume);

    /**
     * 设置配置文件
     *
     * @param mqConfig
     */
    void onConfig(MqConfig mqConfig);
}
