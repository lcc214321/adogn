package com.dongyulong.dogn.core.monitor.indicator;


import com.dongyulong.dogn.common.config.CommonUtils;

/**
 * 监控的类型
 * @author zhangshaolong
 * @create 2021/12/15
 **/
public enum MonitorType {

    /** 线程池**/
    THREAD("thread","app", "endpoint", "thread", "type"),

    /** 方法的监控 **/
    METHOD("method","app", "endpoint", "method", "type"),

    /** http的监控 **/
    HTTP("http","app", "endpoint", "http", "type"),

    /**缓存监控**/
    CACHE("cache","app", "endpoint", "cache", "type");

    private String[] labelNames;

    private String monitor;

    MonitorType(String monitor,String... labelNames) {
        this.monitor = monitor;
        this.labelNames = labelNames;
    }

    public void monitor(String monitorName,ValueType valueType,Double v) {
        switch (this) {
            case HTTP:
            case METHOD:
            case THREAD:
            case CACHE:
                valueType.addValue(v, CommonUtils.getAppName(), CommonUtils.getHostName(),monitorName,monitor);
                break;
            default:
                break;
        }
    }

    public String getMonitor() {
        return monitor;
    }

    public String[] getLabelNames() {
        return labelNames;
    }
}
