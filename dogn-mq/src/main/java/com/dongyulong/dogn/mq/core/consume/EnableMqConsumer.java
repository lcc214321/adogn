package com.dongyulong.dogn.mq.core.consume;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启mq的消费者模式信息
 *
 * @author zhangshaolong
 * @create 2022/1/17
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(MqConfiguration.class)
public @interface EnableMqConsumer {
}
