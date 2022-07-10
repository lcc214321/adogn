package com.dongyulong.dogn.core.monitor;

import com.dongyulong.dogn.core.monitor.indicator.Indicator;
import com.dongyulong.dogn.core.monitor.indicator.IndicatorCollector;
import com.dongyulong.dogn.core.monitor.indicator.MonitorType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 监控基础类
 *
 * @author zhangshaolong
 * @create 2021/12/15
 */
public interface Monitor {

    /**
     * 监控类型
     *
     * @return
     */
    MonitorType monitorType();

    /**
     * 初始化监控信息
     */
    default void initMonitor() {
        List<Indicator> indicatorList = Indicator.getListIndicator(monitorType());
        if (CollectionUtils.isEmpty(indicatorList)) {
            return;
        }
        indicatorList.forEach(indicator -> {
            //TODO注册指标数据信息
            indicator.getValueType().register(indicator.getName(), indicator.getName(), indicator.getMonitorType().getLabelNames());
        });
    }

    /**
     * 名称
     *
     * @return
     */
    String monitorName();

    /**
     * 上报数据
     *
     * @param collector
     */
    void report(IndicatorCollector collector);


    /**
     * 获取监控key
     *
     * @return
     */
    default String getAction() {
        return monitorType() + monitorName();
    }

}
