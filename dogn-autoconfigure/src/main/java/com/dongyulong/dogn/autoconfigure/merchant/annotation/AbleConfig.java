package com.dongyulong.dogn.autoconfigure.merchant.annotation;

import org.springframework.util.Base64Utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记为可配置的
 *
 * @author dongy
 * @date 16:37 2022/2/16
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AbleConfig {

    /**
     * 是否需要解密
     *
     * @return return decrypt
     */
    boolean decrypt() default true;

    /**
     * 是否为文件
     * <p>
     * <p>
     * 标记为文件则
     * <p>
     * 1.在加密当前属性时必须为http链接,否则抛出{@link NoSuchFieldException}异常
     * <p>
     * 2.在解密时如果是{@link String}类型,则为标准{@linkplain Base64Utils#decode(byte[]) base64编码的}后字符串
     *
     * @return return decrypt
     */
    boolean file() default false;
}
