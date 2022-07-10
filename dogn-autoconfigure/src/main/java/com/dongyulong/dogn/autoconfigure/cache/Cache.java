package com.dongyulong.dogn.autoconfigure.cache;


import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存
 *
 * @author zhang.shaolong
 * @create 2021/12/16
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Cache {

    /**
     * 缓存名称
     *
     * @return
     */
    String name();

    /**
     * 缓存大小，默认512
     * @return
     */
    int maxSize() default 512;

    /**
     * 读取过期时间，默认5分钟
     *
     * @return
     */
    long expireAfterWrite() default 10L;

    /**
     * 过期刷新时间，默认5分钟
     *
     * @return
     */
    long refreshAfterWrite() default 5L;

    /**
     * 时间单位，默认分钟
     *
     * @return
     */
    TimeUnit timeUnit()  default TimeUnit.MINUTES;
}
