package com.dongyulong.dogn.mq.auto;

import com.dongyulong.dogn.juno.core.Juno;
import com.dongyulong.dogn.mq.common.AbstractRocketMQSubscriber;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/93:14 下午
 * @since v1.0
 */
@Slf4j
@AllArgsConstructor
public class RocketmqRunListener implements SpringApplicationRunListener {

    private SpringApplication application;
    private String[] args;

    @Override
    public void starting() {
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
    }

    @Override
    public void finished(ConfigurableApplicationContext context, Throwable exception) {
        Map<String, Object> metaInfo = new HashMap<>(2);
        List<MetaData> metaDatas = Lists.newArrayList();
        try {
            Map<String, AbstractRocketMQSubscriber> consumers = context.getBeansOfType(AbstractRocketMQSubscriber.class);
            if (consumers != null && !consumers.isEmpty()) {
                consumers.values().forEach(consumer -> {
                    MetaData metaData = new MetaData(consumer.getGroup(), Sets.newHashSet(consumer.getTopic()));
                    metaDatas.add(metaData);
                });
                metaInfo.put("consumers", metaDatas);
                Juno.setMeta("rocketmq", metaInfo);
            }
        } catch (Exception e) {
            log.warn("Update rmq juno info failed!", e);
        }
    }

    @Data
    @AllArgsConstructor
    private static class MetaData {
        private String name;
        private Set<String> topics;
    }
}
