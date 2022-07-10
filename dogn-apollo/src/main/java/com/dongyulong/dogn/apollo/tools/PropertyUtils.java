package com.dongyulong.dogn.apollo.tools;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/96:55 下午
 * @since v1.0
 */
public class PropertyUtils implements EnvironmentAware {

    static Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        PropertyUtils.environment = environment;
    }

    /**
     * 获取配置值, 如果不存在会返回空串
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return environment.getProperty(key, StringUtils.EMPTY);
    }

    /**
     * 获取配置值, 如果不存在会返回传入的默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    /**
     * 获取配置值, 如果不存在会返回NULL
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getProperty(String key, Class<T> clazz) {
        return environment.getProperty(key, clazz, null);
    }

    /**
     * 获取配置值, 如果不存在会返回传入默认值
     *
     * @param key
     * @param clazz
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static <T> T getProperty(String key, Class<T> clazz, T defaultValue) {
        return environment.getProperty(key, clazz, defaultValue);
    }

}
