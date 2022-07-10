package com.dongyulong.dogn.autoconfigure.monitor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 监控注解
 * @author zhangshaolong
 * @create 2021/12/15
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitor {

    /**
     * 打点统计的方法数据
     * @return
     */
    String value() default "";

    /**
     * 超时时间的统计信息
     * @return
     */
    long time() default 500;
}
