package com.dongyulong.dogn.core.monitor.indicator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计打点数据,可以计算每个维度的数据统计信息
 * @author zhangshaolong
 * @create 2021/12/15
 */
public class IndicatorCollector {

    /**
     * 按照时间维度聚合在一起，可以按照分钟级别进行统计数据，后续在做调整
     */
    private Map<Indicator, List<Double>> indicatorParams = new LinkedHashMap<>();

    /**
     * 单个的数据维度信息
     */
    private Map<Indicator, Double> indicators = new LinkedHashMap<>();

    /**
     * 单次的去打点数据，有bug数据,需要统计这一秒的请求数据
     * @param indicator
     * @param value
     */
    public void collect(Indicator indicator, double value) {
        indicators.put(indicator, value);
    }

    public Map<Indicator, Double> getIndicators() {
        return indicators;
    }
}
