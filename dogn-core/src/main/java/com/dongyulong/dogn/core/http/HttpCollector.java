package com.dongyulong.dogn.core.http;

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
 * 自定义http的采集信息
 *
 * @author zhangshaolong
 * @create 2022/1/29
 **/
@Slf4j
public class HttpCollector extends Collector {

    protected final ConcurrentMap<String, HttpMonitor> children = new ConcurrentHashMap<>();

    public void addHttpMonitor(String httpName, HttpMonitor httpMonitor) {
        children.put(httpName, httpMonitor);
        log.info("HttpCollector add monitor:{}", httpName);
    }

    @Override
    public List<MetricFamilySamples> collect() {
        //TODO 自定义http信息的采集数据信息
        List<MetricFamilySamples> mfs = new ArrayList<>();
        List<String> labelNames = Arrays.asList("appname", "endpoint", "httpname");

        GaugeMetricFamily qpsMetric = new GaugeMetricFamily(Indicator.HTTP_QPS.getName(),
                Indicator.HTTP_QPS.getName(), labelNames);
        mfs.add(qpsMetric);

        GaugeMetricFamily code4Metric = new GaugeMetricFamily(Indicator.HTTP_CODE_4.getName(),
                Indicator.HTTP_CODE_4.getName(), labelNames);
        mfs.add(code4Metric);

        GaugeMetricFamily code5Metric = new GaugeMetricFamily(Indicator.HTTP_CODE_5.getName(),
                Indicator.HTTP_CODE_5.getName(), labelNames);
        mfs.add(code5Metric);

        GaugeMetricFamily errorMetric = new GaugeMetricFamily(Indicator.HTTP_ERROR.getName(),
                Indicator.HTTP_ERROR.getName(), labelNames);
        mfs.add(errorMetric);

        SummaryMetricFamily timeMetric = new SummaryMetricFamily(Indicator.HTTP_TIME.getName(),
                Indicator.HTTP_TIME.getName(), labelNames);
        mfs.add(timeMetric);

        for (Map.Entry<String, HttpMonitor> c : children.entrySet()) {
            List<String> httpName = Arrays.asList(CommonUtils.getAppName(), CommonUtils.getHostName(), c.getKey());
            HttpMonitor httpMonitor = c.getValue();
            if (log.isDebugEnabled()) {
                log.debug("httpMonitor-{}:{}", c.getKey(), httpMonitor.toString());
            }
            Counter.CounterResult counterResult = httpMonitor.qpsCounter.changed();
            qpsMetric.addMetric(httpName, counterResult.changed);
            timeMetric.addMetric(httpName, counterResult.changed, counterResult.times);
            code4Metric.addMetric(httpName, httpMonitor.code4xx.changed().changed);
            code5Metric.addMetric(httpName, httpMonitor.code5xx.changed().changed);
            errorMetric.addMetric(httpName, httpMonitor.failCounter.changed().changed);
        }
        return mfs;
    }
}
