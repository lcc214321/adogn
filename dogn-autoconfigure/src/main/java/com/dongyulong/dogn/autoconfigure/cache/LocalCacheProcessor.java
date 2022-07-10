package com.dongyulong.dogn.autoconfigure.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 本地缓存使用
 * @author zhang.shaolong
 * @create 2021/11/18
 **/
@Slf4j
public class LocalCacheProcessor extends LocalCacheManager implements BeanPostProcessor {

    /**
     * bean 初始化之前执行业务
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //获取cache信息
        Cache cache = bean.getClass().getAnnotation(Cache.class);
        if (cache == null) {
            return bean;
        }

        //TODO 
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}

