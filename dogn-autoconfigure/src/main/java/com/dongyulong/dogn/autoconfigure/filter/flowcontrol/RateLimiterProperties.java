package com.dongyulong.dogn.autoconfigure.filter.flowcontrol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RateLimiterProperties implements InitializingBean, ApplicationListener<ApplicationEvent> {
    @Getter
    @Setter
    private String flowControlProperty;

    @Getter
    private Map<String, Config> configs;

    @Override
    public void afterPropertiesSet() throws Exception {
        loadLimitersFromConfig();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof EnvironmentChangeEvent) {
            log.info(((EnvironmentChangeEvent) applicationEvent).getKeys().toString());
            if (((EnvironmentChangeEvent) applicationEvent).getKeys().contains("flowcontrol.hotparams.flowControlProperty")) {
                loadLimitersFromConfig();
            }
        }
    }

    private void loadLimitersFromConfig() {
        log.info("flowControlProperty: {}", flowControlProperty);
        if (StringUtils.isEmpty(flowControlProperty)) {
            return;
        }
        try {
            configs = JSON.parseObject(flowControlProperty, new TypeReference<Map<String, Config>>() {
            });
        } catch (Exception e) {
            log.error("parse ratelimiter config failed!, {}", configs, e);
        }
    }

    @Getter
    @Setter
    public static class Config {
        private int rate;
        private int timeWindow = 1;
        private char timeUnit = 'S';

        public int getTimeWindowSeconds() {
            int seconds = 0;
            switch (timeUnit) {
                case 'S':
                case 's':
                    seconds = timeWindow;
                    break;
                case 'M':
                case 'm':
                    seconds = (int) TimeUnit.MINUTES.toSeconds(timeWindow);
                    break;
                case 'H':
                case 'h':
                    seconds = (int) TimeUnit.HOURS.toSeconds(timeWindow);
                    break;
                case 'D':
                case 'd':
                    seconds = (int) TimeUnit.DAYS.toSeconds(timeWindow);
                    break;
                default:
                    throw new IllegalArgumentException("Illegal Timeunit: " + timeUnit);
            }
            return seconds;
        }
    }
}
