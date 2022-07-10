package com.dongyulong.dogn.core.monitor.indicator;

import io.prometheus.client.Collector;

import java.util.List;

/**
 * 自定义的监控维度信息
 *
 * @author zhangshaolong
 * @create 2022/1/28
 **/
public class DognCollector extends Collector {

    /**
     * 自定义的采集信息
     *
     * @return
     */
    @Override
    public List<MetricFamilySamples> collect() {
        return null;
    }
}
