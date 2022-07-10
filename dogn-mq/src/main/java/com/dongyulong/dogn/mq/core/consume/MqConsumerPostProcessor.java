package com.dongyulong.dogn.mq.core.consume;

import com.dongyulong.dogn.mq.core.consume.listener.IMqListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.Environment;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangshaolong
 * @create 2022/1/17
 */
@Slf4j
public class MqConsumerPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements EnvironmentAware, ApplicationListener<ContextClosedEvent> {

    private Environment environment;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        if (IMqListener.class.isAssignableFrom(targetClass)) {
            MqConsumer mqConsumer = targetClass.getAnnotation(MqConsumer.class);
            if (mqConsumer != null) {
                log.info("create mq consumer for {}", targetClass.getSimpleName());
                try {
                    createConsumer(mqConsumer, (IMqListener) bean);
                } catch (MQClientException e) {
                    log.error("create mq consumer error", e);
                }
            }
        }
        return bean;
    }

    /**
     * 创建mq 监听服务
     *
     * @param mqConsumer
     * @param iMqListener
     */
    private void createConsumer(MqConsumer mqConsumer, IMqListener iMqListener) throws MQClientException {
        //配置信息
        MqConfig configs =
                new MqConfig(mqConsumer.topic(), mqConsumer.group(), mqConsumer.tag());
        configs.setConsumeThreadMax(mqConsumer.consumeThreadMax());
        configs.setConsumeThreadMin(mqConsumer.consumeThreadMin());
        configs.setBroadcast(mqConsumer.isBroadcast());
        configs.setPullBatchSize(mqConsumer.pullBatchSize());
        configs.setMaxReconsumeTimes(mqConsumer.retryTime());
        iMqListener.onConfig(configs);
        createConsumer(configs, iMqListener);
    }

    /**
     * 创建mq的监听服务
     *
     * @param configs
     * @param iMqListener
     * @throws MQClientException
     */
    private void createConsumer(MqConfig configs, IMqListener iMqListener) throws MQClientException {
        DefaultMQPushConsumer consumer = MqClientService.getDefaultMQPushConsumer(configs);
        consumer.setMessageListener(iMqListener);
        MqStartListener.add(consumer);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                consumer.shutdown();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            log.info("ShutdownHook Executed...");
        }));
        log.info("group:{} topic:{} tag:{} create consume success", configs.getGroup(), configs.getTopic(), configs.getTags());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        MqStartListener.stop();
    }
}
