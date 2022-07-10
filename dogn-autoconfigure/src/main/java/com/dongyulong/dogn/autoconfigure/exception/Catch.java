package com.dongyulong.dogn.autoconfigure.exception;

import com.dongyulong.dogn.common.result.Result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异常捕获
 * @author zhangshaolong
 * @create 2021/11/19
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Catch {

    /**
     * 捕获的异常信息
     * @return
     */
    Class<? extends Throwable>[] excetions()  default Exception.class;

    /**
     * 打点统计,有数据了就会上报信息
     * @return
     */
    String value() default "";

    /**
     * 返回类型，适用新的版本接口返回值信息
     * @return
     */
    Class type()  default Result.class;

}
