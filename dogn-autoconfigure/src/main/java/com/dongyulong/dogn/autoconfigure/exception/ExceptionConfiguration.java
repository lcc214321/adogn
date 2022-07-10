package com.dongyulong.dogn.autoconfigure.exception;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

import static org.springframework.boot.web.servlet.FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER;

/**
 * 异常的处理拦截
 * @author zhangshaolong
 * @create 2021/11/22
 **/
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ExceptionConfiguration {

    @Bean
    @Order(REQUEST_WRAPPER_FILTER_MAX_ORDER - 1024)
    public CatchHandle catchHandle() {
        return new CatchHandle();
    }
}
