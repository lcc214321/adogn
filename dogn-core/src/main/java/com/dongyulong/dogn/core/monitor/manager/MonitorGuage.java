package com.dongyulong.dogn.core.monitor.manager;

import com.dongyulong.dogn.core.monitor.indicator.IndicatorCollector;
import com.dongyulong.dogn.core.monitor.indicator.MonitorType;
import com.dongyulong.dogn.core.monitor.indicator.ValueType;
import lombok.extern.slf4j.Slf4j;

/**
 * 监控打点数据
 *
 * @author zhangshaolong
 * @create 2021/12/15
 **/
@Slf4j
public class MonitorGuage {

    private static final String ACTION_PREFIX = "agaue";

    private MonitorType monitorType;

    private String monitorName;

    public MonitorGuage(MonitorType monitorType, String monitorName) {
        this.monitorType = monitorType;
        this.monitorName = getMonitorAction(monitorName);
    }

    /**
     * 每秒提交一次数据信息,基础平台的服务去打点数据日志信息
     *
     * @param collector
     */
    public void commitBySecond(IndicatorCollector collector) {
        collector.getIndicators().forEach((k, v) -> {
            ValueType valueType = k.getValueType();
            monitorType.monitor(monitorName, valueType, v);
        });
    }

    private String getMonitorAction(String monitorName) {
        return ACTION_PREFIX + "_" + monitorName.toLowerCase();
    }
}
