package com.dongyulong.dogn.autoconfigure.filter;

import com.dongyulong.dogn.autoconfigure.filter.flowcontrol.RedisRateLimiter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/9 7:53 上午
 * @since v1.0
 */
@Configuration
@ConditionalOnClass({RedisRateLimiter.class})
@ConditionalOnBean({RedisRateLimiter.class})
@ConditionalOnProperty(prefix = "dida.monitor", name = "http", havingValue = "true")
public class WebLimitFilterConfig {

    /**
     * 监控的返回打点
     *
     * @param rateLimiter -
     * @return -
     */
    @Bean
    public WebLimitFilter webLimitFilter(RedisRateLimiter rateLimiter) {
        return new WebLimitFilter(rateLimiter);
    }

}
