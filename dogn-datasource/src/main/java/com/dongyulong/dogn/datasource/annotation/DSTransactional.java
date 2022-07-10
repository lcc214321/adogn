package com.dongyulong.dogn.datasource.annotation;

import com.dongyulong.dogn.datasource.routing.Routing;
import com.dongyulong.dogn.datasource.enums.DatabaseEnum;
import com.dongyulong.dogn.datasource.enums.DatabaseTypeEnum;
import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 选择数据源并开启事务，如果只想选择数据源可以使用注解{@link DS}
 *
 * @author dongy
 * @version v2.0.1
 * @date 15:13 2022/1/6
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Transactional
public @interface DSTransactional {

    /**
     * 后续是数据源的名称信息
     */
    @AliasFor(annotation = DS.class, attribute = "type")
    DatabaseTypeEnum type();

    /**
     * 数据库名称
     */
    @AliasFor(annotation = DS.class, attribute = "name")
    DatabaseEnum name();

    /**
     * 参数{@link Routing}位置
     */
    @AliasFor(annotation = DS.class, attribute = "index")
    int index() default 0;

    @AliasFor(annotation = Transactional.class, attribute = "value")
    String value() default "";

    @AliasFor(annotation = Transactional.class, attribute = "transactionManager")
    String transactionManager() default "";

    @AliasFor(annotation = Transactional.class, attribute = "propagation")
    Propagation propagation() default Propagation.REQUIRED;

    @AliasFor(annotation = Transactional.class, attribute = "isolation")
    Isolation isolation() default Isolation.DEFAULT;

    @AliasFor(annotation = Transactional.class, attribute = "timeout")
    int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

    @AliasFor(annotation = Transactional.class, attribute = "readOnly")
    boolean readOnly() default false;

    @AliasFor(annotation = Transactional.class, attribute = "rollbackFor")
    Class<? extends Throwable>[] rollbackFor() default Exception.class;

    @AliasFor(annotation = Transactional.class, attribute = "rollbackForClassName")
    String[] rollbackForClassName() default {};

    @AliasFor(annotation = Transactional.class, attribute = "noRollbackFor")
    Class<? extends Throwable>[] noRollbackFor() default {};

    @AliasFor(annotation = Transactional.class, attribute = "noRollbackForClassName")
    String[] noRollbackForClassName() default {};
}
