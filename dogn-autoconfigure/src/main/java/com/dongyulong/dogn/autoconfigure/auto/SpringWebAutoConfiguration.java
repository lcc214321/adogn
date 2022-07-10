package com.dongyulong.dogn.autoconfigure.auto;

import com.dongyulong.dogn.autoconfigure.controller.DognController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangshaolong
 * @create 2021/12/20
 **/
@Configuration
@ConditionalOnWebApplication
public class SpringWebAutoConfiguration {

    /**
     * 异常的服务信息
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "dida.monitor", name = "http", havingValue = "true")
    public DognController agaueController() {
        return new DognController();
    }
}
