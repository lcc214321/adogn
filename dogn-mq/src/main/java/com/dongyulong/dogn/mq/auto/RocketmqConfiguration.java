package com.dongyulong.dogn.mq.auto;

import com.dongyulong.dogn.mq.common.SpringConext;
import com.dongyulong.dogn.mq.entities.RocketMQConsumerConfig;
import com.dongyulong.dogn.mq.entities.RocketMQProducerConfig;
import com.dongyulong.dogn.mq.helper.RocketMQProducerHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/93:15 下午
 * @since v1.0
 */
@Configuration
@Slf4j
public class RocketmqConfiguration {

    @Bean
    @ConfigurationProperties(value = "rocketmq.producer", ignoreUnknownFields = false)
    public RocketMQProducerConfig rocketMQProducerConfig() {
        return new RocketMQProducerConfig();
    }

    @Bean
    @ConfigurationProperties(value = "rocketmq.consumer", ignoreUnknownFields = false)
    public RocketMQConsumerConfig rocketMQConsumerConfig() {
        return new RocketMQConsumerConfig();
    }

//    @Bean
//    public TxManagerHelper txManagerHelper(){
//        return new TxManagerHelper();
//    }

    @EventListener
    public void onRefresh(RefreshScopeRefreshedEvent event) {
        RocketMQProducerHelper.refresh();
        log.info("RocketMQProducerHelper config refreshed!");
    }

    @Bean
    public SpringConext springConext() {
        return new SpringConext();
    }
}
