package com.dongyulong.dogn.core.method;

import com.dongyulong.dogn.common.config.CommonUtils;
import com.dongyulong.dogn.core.monitor.Counter;
import com.dongyulong.dogn.core.monitor.indicator.Indicator;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import io.prometheus.client.SummaryMetricFamily;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 自定义方法的采集信息
 *
 * @author zhangshaolong
 * @create 2022/1/29
 **/
@Slf4j
public class MethodCollector extends Collector {

    protected final ConcurrentMap<String, MethodMonitor> children = new ConcurrentHashMap<>();

    public void addMethodMonitor(String monitorName, MethodMonitor methodMonitor) {
        children.put(monitorName, methodMonitor);
        log.info("MethodCollector add monitor:{}", monitorName);
    }

    @Override
    public List<MetricFamilySamples> collect() {

        List<MetricFamilySamples> mfs = new ArrayList<MetricFamilySamples>();
        List<String> labelNames = Arrays.asList("appname", "endpoint", "method");
        GaugeMetricFamily gaugeErrorMetric = new GaugeMetricFamily(Indicator.METHOD_ERROR.getName(),
                Indicator.METHOD_ERROR.getName(), labelNames);
        mfs.add(gaugeErrorMetric);

        SummaryMetricFamily summaryTimeMetric = new SummaryMetricFamily(Indicator.METHOD_TIME.getName(),
                Indicator.METHOD_TIME.getName(), labelNames);
        mfs.add(summaryTimeMetric);

        GaugeMetricFamily gaugeSuccessMetric = new GaugeMetricFamily(Indicator.METHOD_SUCCESS.getName(),
                Indicator.METHOD_SUCCESS.getName(), labelNames);
        mfs.add(gaugeSuccessMetric);

        GaugeMetricFamily gaugeQpsMetric = new GaugeMetricFamily(Indicator.METHOD_QPS.getName(),
                Indicator.METHOD_QPS.getName(), labelNames);
        mfs.add(gaugeQpsMetric);


        for (Map.Entry<String, MethodMonitor> c : children.entrySet()) {

            List<String> cacheName = Arrays.asList(CommonUtils.getAppName(), CommonUtils.getHostName(), c.getKey());
            MethodMonitor methodMonitor = c.getValue();
            if (log.isDebugEnabled()) {
                log.debug("methodMonitor-{}:{}", c.getKey(), methodMonitor.toString());
            }
            gaugeErrorMetric.addMetric(cacheName, methodMonitor.getFailResult());
            gaugeSuccessMetric.addMetric(cacheName, methodMonitor.getSuccResult());
            Counter.CounterResult result = methodMonitor.getResult();
            gaugeQpsMetric.addMetric(cacheName, result.changed);
            summaryTimeMetric.addMetric(cacheName, result.changed, result.times);
        }
        return mfs;
    }
}
