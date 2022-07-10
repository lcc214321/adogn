package com.dongyulong.dogn.autoconfigure.cache;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhang.shaolong
 * @create 2021/12/16
 **/
@Configuration
public class CacheConfiguration {

    /**
     * 缓存服务的初始化
     * @return
     */
    @Bean
    public BeanPostProcessor cacheConfigAnnotationProcessor() {
        return new LocalCacheProcessor();
    }
}
