package com.dongyulong.dogn.mq.core.consume;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangshaolong
 * @create 2022/1/17
 */
@Configuration
public class MqConfiguration {

    /**
     * 启动mq的扫描信息
     * @return
     */
    @Bean
    public MqConsumerPostProcessor mqConsumerPostProcessor() {
        return new MqConsumerPostProcessor();
    }

    @Bean
    public MqStartListener mqStartListener() {
        return new MqStartListener();
    }

}
