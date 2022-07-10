package com.dongyulong.dogn.core.method;

import com.dongyulong.dogn.core.monitor.Counter;
import com.dongyulong.dogn.core.monitor.Monitor;
import com.dongyulong.dogn.core.monitor.indicator.Indicator;
import com.dongyulong.dogn.core.monitor.indicator.IndicatorCollector;
import com.dongyulong.dogn.core.monitor.indicator.MonitorType;
import io.prometheus.client.CollectorRegistry;

/**
 * 方法的监控
 *
 * @author zhangshaolong
 * @create 2021/12/15
 **/
public class DognMethod implements Monitor {

    private final static MethodCollector methodCollector = new MethodCollector().register(CollectorRegistry.defaultRegistry);

    private MethodMonitor monitor = new MethodMonitor();

    /**
     * 打点的方法名
     */
    private String methodName;

    public DognMethod(String methodName) {
        this.methodName = methodName;
        methodCollector.addMethodMonitor(methodName, monitor);
    }

    @Override
    public MonitorType monitorType() {
        return MonitorType.METHOD;
    }

    @Override
    public String monitorName() {
        return methodName;
    }

    public MethodMonitor getMonitor() {
        return monitor;
    }

    @Override
    public void report(IndicatorCollector collector) {
        Counter.CounterResult result = monitor.getResult();
        collector.collect(Indicator.METHOD_ERROR, monitor.getFailResult());
        collector.collect(Indicator.METHOD_SUCCESS, monitor.getSuccResult());
        collector.collect(Indicator.METHOD_QPS, result.changed);
        collector.collect(Indicator.METHOD_TIME, result.avgCostMilSeconds);
    }
}
