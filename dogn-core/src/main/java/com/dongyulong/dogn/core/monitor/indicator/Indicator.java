package com.dongyulong.dogn.core.monitor.indicator;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 监控打点的数据类型数据
 * @author zhangshaolong
 * @create 2021/12/15
 */
public enum Indicator {

    /** 执行次数 **/
    THREAD_SIZE("size",MonitorType.THREAD),

    /** 执行次数 **/
    EXECUTION_COUNT("exe_count",MonitorType.THREAD),

    /** 拒绝次数 **/
    EXECUTION_REJECTED_COUNT("rejected_count",MonitorType.THREAD),

    /** 平均执行时间 **/
    EXECUTION_TIME_MILLIS("exe_time",MonitorType.THREAD,ValueType.HISTOGRAM),

    /** 活跃线程数**/
    ACTIVE_THREAD("active_count",MonitorType.THREAD),

    /** 队列长度 **/
    QUEUE_SIZE("queue",MonitorType.THREAD),

    /** 任务等待时间  **/
    EXECUTION_WAIT_TIME_MILLIS("ewt_time",MonitorType.THREAD,ValueType.HISTOGRAM),

    /** 方法的请求书 **/
    METHOD_QPS("qps",MonitorType.METHOD),

    /** 方法的成功数 **/
    METHOD_SUCCESS("success",MonitorType.METHOD),

    /** 方法的失败数**/
    METHOD_ERROR("error",MonitorType.METHOD),

    /** 方法的执行时间**/
    METHOD_TIME("time",MonitorType.METHOD),

    /** http的请求书 **/
    HTTP_QPS("qps",MonitorType.HTTP),

    /** http的失败数**/
    HTTP_CODE_4("code_4",MonitorType.HTTP),

    HTTP_CODE_5("code_5",MonitorType.HTTP),

    HTTP_ERROR("error",MonitorType.HTTP),

    /** http的执行时间**/
    HTTP_TIME("exe_time",MonitorType.HTTP,ValueType.HISTOGRAM),

    /**  请求量 **/
    CACHE_REQ("cache_req",MonitorType.CACHE),

    /** 命中率 **/
    CACHE_HIT_RATE("cache_hit_rt",MonitorType.CACHE),

    /** 过期数量**/
    CACHE_EVICTION_COUNT("cache_evict",MonitorType.CACHE),

    /** 回填数量 **/
    CACHE_LOAD_COUNT("cache_load",MonitorType.CACHE),

    /**  回填抛出异常 **/
    CACHE_LOAD_FAIL("cache_fail",MonitorType.CACHE),

    /** 回填时间**/
    CACHE_LOAD_TIME_MILLIS("cache_lt_ms",MonitorType.CACHE,ValueType.HISTOGRAM);

    private String shortTitle;

    private MonitorType monitorType;

    private ValueType valueType;

    Indicator(String shortTitle, MonitorType monitorType) {
        this(shortTitle,monitorType,ValueType.GAUGE);
    }

    Indicator(String shortTitle, MonitorType monitorType,ValueType valueType) {
        this.shortTitle = shortTitle;
        this.monitorType = monitorType;
        this.valueType = valueType;
    }


    public MonitorType getMonitorType() {
        return monitorType;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String getName() {
        return monitorType.getMonitor() + "_" + shortTitle;
    }

    public static List<Indicator> getListIndicator(MonitorType monitorType) {
        return Arrays.stream(Indicator.values())
                .filter(type -> type.getMonitorType() == monitorType)
                .collect(Collectors.toList());
    }
}
