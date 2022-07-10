package com.dongyulong.dogn.autoconfigure.log;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;


/**
 * 日志切面
 *
 * @author dongy
 * @date 11:42 2022/2/9
 **/
@Configuration
@AutoConfigureOrder(2)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class MethodLogAutoConfig {

    public static final String PREFIX = "dida.method.log";

    @Setter
    @Getter
    private Boolean enable;

    @Bean
    @Order(2)
    @ConditionalOnProperty(value = "enable", prefix = MethodLogAutoConfig.PREFIX, havingValue = "true")
    public MethodLogAspect methodLogAspect() {
        return new MethodLogAspect();
    }
}
