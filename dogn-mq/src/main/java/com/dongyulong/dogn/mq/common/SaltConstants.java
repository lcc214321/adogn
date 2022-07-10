package com.dongyulong.dogn.mq.common;

import com.dongyulong.dogn.apollo.tools.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author 王雪源
 * @Date 2021/12/20 13:39
 * @Version 1.0
 */
public class SaltConstants {

    public static final String REDIS_GROUP = "[rc9]mq_fail:";

    public static final String REDIS_KEY = "queue";

    public static final String REDIS_QUEUE = REDIS_GROUP + REDIS_KEY;


    /**
     * 默认加密盐值
     */
    public static final String DEFAULT_MD5_SALT = "202CB962AC59075B964B07152D234B70";

    /**
     * 获取默认的appID
     *
     * @return
     */
    public static String getAppId() {
        String appId = PropertyUtils.getProperty("spring.application.name");
        if (StringUtils.isEmpty(appId)) {
            appId = "default";
        }
        return appId;
    }


    /**
     * 判断是否是线上环境
     *
     * @return
     */
    public static boolean online() {
        String active = PropertyUtils.getProperty("spring.profiles.active");
        if (StringUtils.isEmpty(active)) {
            return false;
        }
        return active.equalsIgnoreCase("production");
    }


    /**
     * 获取机器人报警群的信息
     *
     * @return
     */
    public static String alramUrl() {
        String url = PropertyUtils.getProperty("mqfail.dingtalk.url", "https://oapi.dingtalk.com/robot/send?access_token=f70f21b40f1076b5102b27170ad210372c38d29bec8d899f9b0fb68afd067dc4");
        return StringUtils.isEmpty(url) ? "" : url;
    }

}
