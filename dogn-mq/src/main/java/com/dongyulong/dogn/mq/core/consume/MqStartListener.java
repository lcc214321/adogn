package com.dongyulong.dogn.mq.core.consume;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.ArrayList;

/**
 * 启动后校验
 *
 * @author zhangshaolong
 * @create 2022/3/2
 **/
@Slf4j
public class MqStartListener extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationListener<ApplicationReadyEvent>,PriorityOrdered {

    private final static ArrayList<DefaultMQPushConsumer> consumers = Lists.newArrayList();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        start();
    }

    private void start() {
        log.info("start mq consume.......");
        try {
            Thread.sleep(2000);
            for (DefaultMQPushConsumer consumer : consumers) {
                consumer.start();
            }
        } catch (Exception e) {
            log.error("onApplicationEvent start error", e);
        }
    }

    public static void add(DefaultMQPushConsumer defaultMQPushConsumer) {
        consumers.add(defaultMQPushConsumer);
    }

    public static void stop() {
        try {
            consumers.forEach(DefaultMQPushConsumer::shutdown);
        } catch (Exception e) {
            log.error("onApplicationEvent shutdown error", e);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
