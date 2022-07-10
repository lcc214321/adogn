package com.dongyulong.dogn.autoconfigure.tools;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/86:45 下午
 * @since v1.0
 */
public class SpringUtils implements ApplicationContextAware {
    private static ApplicationContext context;

    /**
     * 此方法可以把ApplicationContext对象inject到当前类中作为一个静态成员变量。
     */
    @Override
    public void setApplicationContext(ApplicationContext context)
            throws BeansException {
        SpringUtils.context = context;
    }

    public static ApplicationContext getContext() {
        return context;
    }


    public static <T> T getBean(Class<T> clazz) {
        try {
            return getContext().getBean(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getBean(String beanId) {
        try {
            return getContext().getBean(beanId);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T getBean(String beanId, Class<T> clazz) {
        try {
            return getContext().getBean(beanId, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        try {
            return getContext().getBeansOfType(clazz);
        } catch (Exception e) {
            return null;
        }
    }
}
