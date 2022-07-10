package com.dongyulong.dogn.autoconfigure.merchant.annotation;

import com.dongyulong.dogn.autoconfigure.merchant.entities.ChannelType;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 不同的支付渠道需要处理的逻辑信息
 *
 * @author zhang.shaolong
 * @create 2022/01/17
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Order
public @interface Channel {
    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     *
     * @return the suggested component name, if any
     */
    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";

    /**
     * 支付渠道
     *
     * @return the suggested {@link ChannelType}channel type, if any
     */
    ChannelType channel() default ChannelType.SYSTEM;

    /**
     * 是否为单例
     *
     * @return the suggested isSingleton, if any
     */
    boolean isSingleton() default true;

    /**
     * The order value.
     * <p>Default is {@link Ordered#LOWEST_PRECEDENCE}.
     *
     * @see Ordered#getOrder()
     */
    @AliasFor(annotation = Order.class, attribute = "value")
    int order() default 1;
}