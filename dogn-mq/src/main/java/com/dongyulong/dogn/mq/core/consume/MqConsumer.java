package com.dongyulong.dogn.mq.core.consume;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * mq消费者注解信息
 * @author zhangshaolong
 * @create 2022/1/17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@DependsOn({"rocketMQConsumerConfig"})
public @interface MqConsumer {

    /**
     * 主题
     * @return
     */
    String topic();

    /**
     * 消费组
     * @return
     */
    String group();

    /**
     * 消费tag
     * @return
     */
    String tag() default "*";


    /**
     * 消费最小线程
     * @return
     */
    int consumeThreadMin() default 5;


    /**
     * 消费最大线程
     * @return
     */
    int consumeThreadMax() default 10;


    /**
     * 消费模式
     */
    boolean isBroadcast() default false;

    /**
     * 消费数据信息
     */
    int pullBatchSize() default 10;


    /**
     * 重试次数默认是12次处理,和源码里面是12的保持一直
     */
    int retryTime() default 12;

}
