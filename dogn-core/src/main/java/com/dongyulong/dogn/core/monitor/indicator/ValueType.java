package com.dongyulong.dogn.core.monitor.indicator;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据统计类型
 *
 * @author zhangshaolong
 * @create 2021/12/15
 **/
public enum ValueType {


    /**
     * 计数
     */
    COUNT("count") {
        public void register(String name, String help, String... labelNames) {
            synchronized (countObject) {
                Counter counter = COUNTER_MAP.get(name);
                if (counter == null) {
                    counter = registerCounter(name, help, labelNames);
                }
                if (counter != null) {
                    COUNTER_MAP.putIfAbsent(name, counter);
                }
            }
        }
    },


    /**
     * 直方图
     */
    HISTOGRAM("histogram"){
        public void register(String name, String help, String... labelNames) {
            synchronized (histogramObject) {
                Histogram histogram = HIS_MAP.get(name);
                if (histogram == null) {
                    histogram = registerHistogram(name, help, labelNames);
                }
                if (histogram != null) {
                    HIS_MAP.putIfAbsent(name, histogram);
                }
            }
        }
    },

    /**
     * 仪表盘
     */
    GAUGE("gauge") {
        public void register(String name, String help, String... labelNames) {
            synchronized (gaugeObject) {
                Gauge gauge = GAUGE_MAP.get(name);
                if (gauge == null) {
                    gauge = registerGauge(name, help, labelNames);
                }
                if (gauge != null) {
                    GAUGE_MAP.putIfAbsent(name, gauge);
                }
            }
        }
    };

    private String name;

    private ValueType(String name) {
        this.name = name;
    }

    private static final ConcurrentHashMap<String, Counter> COUNTER_MAP = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, Histogram> HIS_MAP = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, Gauge> GAUGE_MAP = new ConcurrentHashMap<>();

    private static final Object countObject = new Object();

    private static final Object histogramObject = new Object();

    private static final Object gaugeObject = new Object();


    /**
     * 添加数据信息
     * @param value
     * @param labelValues
     */
    public void addValue(Double value, String... labelValues) {
        if (null == value) {
            value = 1D;
        }
        switch (name) {
            case "count":
                Counter counter = COUNTER_MAP.get(name);
                if (counter!=null) {
                    counter.labels(labelValues).inc(value);
                }
                break;
            case "histogram":
                Histogram histogram = HIS_MAP.get(name);
                if (histogram!=null) {
                    histogram.labels(labelValues).observe(value);
                }
                break;
            case "gauge":
                Gauge gauge = GAUGE_MAP.get(name);
                if (gauge!=null) {
                    gauge.labels(labelValues).set(value);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 构造方法
     * @param name
     * @param help
     * @param labelNames
     */
    public abstract void register(String name, String help, String... labelNames);

    /**
     * 注册不同count的数据信息
     *
     * @param name
     * @param help
     * @param labelNames
     * @return
     */
    private static Counter registerCounter(String name, String help, String... labelNames) {
        return Counter.build().name(name).help(help).labelNames(labelNames).register();
    }

    /**
     * 注册直方图
     *
     * @param name
     * @param help
     * @param labelNames
     * @return
     */
    private static Histogram registerHistogram(String name, String help, String... labelNames) {
        return Histogram.build().name(name).help(help).labelNames(labelNames).register();
    }

    /**
     * 注册仪表盘
     *
     * @param name
     * @param help
     * @param labelNames
     * @return
     */
    private static Gauge registerGauge(String name, String help, String... labelNames) {
        return Gauge.build().name(name).help(help).labelNames(labelNames).register();
    }


}
