package com.dongyulong.dogn.core.executor;

import com.dongyulong.dogn.common.config.CommonUtils;
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
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 采集线程相关的数据信息
 * @author zhangshaolong
 * @create 2022/4/21
 **/
@Slf4j
public class ExecutorCollector extends Collector {

    private static final ExecutorCollector INSTANCE = new ExecutorCollector();

    public static ExecutorCollector getInstance() {
        return INSTANCE;
    }

    protected final ConcurrentMap<String, DognExecutor> children = new ConcurrentHashMap<>();

    public void addExecuteMonitor(String monitorName, DognExecutor dognExecutor) {
        if (!children.containsKey(monitorName)) {
            children.putIfAbsent(monitorName, dognExecutor);
            log.info("ExecutorCollector add monitor:{}", monitorName);
        }
    }

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();
        List<String> labelNames = Arrays.asList("appname","endpoint","exename");
        GaugeMetricFamily threadMetric = new GaugeMetricFamily(Indicator.THREAD_SIZE.getName(),
                Indicator.THREAD_SIZE.getName(), labelNames);
        mfs.add(threadMetric);

        GaugeMetricFamily executeMetric = new GaugeMetricFamily(Indicator.EXECUTION_COUNT.getName(),
                Indicator.EXECUTION_COUNT.getName(), labelNames);
        mfs.add(executeMetric);

        GaugeMetricFamily rejectedMetric = new GaugeMetricFamily(Indicator.EXECUTION_REJECTED_COUNT.getName(),
                Indicator.EXECUTION_REJECTED_COUNT.getName(), labelNames);
        mfs.add(rejectedMetric);

        GaugeMetricFamily activeMetric = new GaugeMetricFamily(Indicator.ACTIVE_THREAD.getName(),
                Indicator.ACTIVE_THREAD.getName(), labelNames);
        mfs.add(activeMetric);

        GaugeMetricFamily queueMetric = new GaugeMetricFamily(Indicator.QUEUE_SIZE.getName(),
                Indicator.QUEUE_SIZE.getName(), labelNames);
        mfs.add(queueMetric);

        SummaryMetricFamily exetimeMetric = new SummaryMetricFamily(Indicator.EXECUTION_TIME_MILLIS.getName(),
                Indicator.EXECUTION_TIME_MILLIS.getName(), labelNames);
        mfs.add(exetimeMetric);

        SummaryMetricFamily exewaitTimeMetric = new SummaryMetricFamily(Indicator.EXECUTION_WAIT_TIME_MILLIS.getName(),
                Indicator.EXECUTION_WAIT_TIME_MILLIS.getName(), labelNames);
        mfs.add(exewaitTimeMetric);

        for(Map.Entry<String, DognExecutor> c: children.entrySet()) {
            List<String> executeName = Arrays.asList(CommonUtils.getAppName(),CommonUtils.getHostName(),c.getKey());
            DognExecutor dognExecutor = c.getValue();
            AbstractDognExecutor.ExecutorMonitor monitor = dognExecutor.monitorRef.getAndSet(new AbstractDognExecutor.ExecutorMonitor());
            ThreadPoolExecutor executor = (ThreadPoolExecutor) dognExecutor.getExecutor();
            long executionCount = monitor.executionCounter.executionCount();
            threadMetric.addMetric(executeName,executor.getCorePoolSize());
            activeMetric.addMetric(executeName,executor.getActiveCount());
            queueMetric.addMetric(executeName,executor.getQueue().size());
            executeMetric.addMetric(executeName,executionCount);
            rejectedMetric.addMetric(executeName,monitor.rejectedCounter.get());
            exetimeMetric.addMetric(executeName, executionCount,monitor.executionCounter.timeSum());
            exewaitTimeMetric.addMetric(executeName,executionCount, monitor.waitedTime.get());
        }
        return mfs;
    }
}
