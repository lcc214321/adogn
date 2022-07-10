package com.dongyulong.dogn.autoconfigure.monitor.common;

import com.dongyulong.dogn.tools.system.SystemPropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取服务的基础信息
 *
 * @author zhangshaolong
 * @create 2022/1/24
 */
public class AppCommon {

    /**
     * 获取appName
     *
     * @return
     */
    public static String getAppName() {
        String appId = SystemPropertyUtils.getProperty("spring.application.name");
        if (StringUtils.isEmpty(appId)) {
            appId = "default";
        }
        return appId;
    }

    /**
     * 获取机器hostName
     *
     * @return
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        }
        return "";
    }


    /**
     * 获取配置信息报警信息
     *
     * @return
     */
    public static double getAlarm() {
        return Double.parseDouble(SystemPropertyUtils.getProperty("dida.agaue.alarm", "0.5"));
    }

    /**
     * 获取配置的线程数
     *
     * @return
     */
    public static int getThread() {
        return Integer.parseInt(SystemPropertyUtils.getProperty("dida.agaue.thread", "20"));
    }

    /**
     * 获取配置信息报警信息
     *
     * @return
     */
    public static Integer getSlow() {
        return Integer.parseInt(SystemPropertyUtils.getProperty("dida.agaue.slow", "100"));
    }

    /**
     * 获取配置信息报警信息
     *
     * @return
     */
    public static Integer getMonitor() {
        return Integer.parseInt(SystemPropertyUtils.getProperty("dida.agaue.sleep", "60"));
    }

    /**
     * 是否需要报警信息
     *
     * @return
     */
    public static Boolean monitor() {
        return Boolean.parseBoolean(SystemPropertyUtils.getProperty("dida.agaue.monitor", "true"));
    }


    /**
     * 获取机器人报警群的信息
     *
     * @return
     */
    public static String alramUrl() {
        String url = SystemPropertyUtils.getProperty("dida.agaue.dingtalk.url", "https://oapi.dingtalk.com/robot/send?access_token=d92e575c2021397a274ff120a3e8d8596cd0d1c19d22bc3635ddfd6b3484242b");
        return StringUtils.isEmpty(url) ? "" : url;
    }

}
