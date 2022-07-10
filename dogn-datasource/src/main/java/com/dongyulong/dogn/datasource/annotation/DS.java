package com.dongyulong.dogn.datasource.annotation;


import com.dongyulong.dogn.datasource.routing.Routing;
import com.dongyulong.dogn.datasource.enums.DatabaseEnum;
import com.dongyulong.dogn.datasource.enums.DatabaseTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 数据源注解切换，如果需要使用事务，可以选择注解{@link DSTransactional}
 *
 * @author dongy
 * @version v2.0.1
 * @date 14:19 2022/1/6
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DS {

    /**
     * 后续是数据源的名称信息
     */
    DatabaseTypeEnum type();

    /**
     * 数据库名称
     */
    DatabaseEnum name();

    /**
     * 参数{@link Routing}位置
     */
    int index() default 0;
}