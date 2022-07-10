package com.dongyulong.dogn.core.monitor.manager;

import com.dongyulong.dogn.core.monitor.Monitor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 不同监控类型的聚合
 *
 * @author zhangshaolong
 * @create 2021/12/15
 **/
public class MonitorManager {

    private static final MonitorManager INSTANCE = new MonitorManager();

    public static MonitorManager getInstance() {
        return INSTANCE;
    }

    private Map<String, MonitorGuage> monitorGroupMap = new ConcurrentHashMap<>();

    private MonitorManager() {
    }

    public static MonitorGuage getMonitor(Monitor monitor) {
        String action = monitor.getAction();
        MonitorGuage monitorGuage = INSTANCE.monitorGroupMap.get(action);
        if (monitorGuage == null) {
            synchronized (INSTANCE) {
                if ((monitorGuage = INSTANCE.monitorGroupMap.get(action)) == null) {
                    monitorGuage = new MonitorGuage(monitor.monitorType(), monitor.monitorName());
                    INSTANCE.monitorGroupMap.put(action, monitorGuage);
                }
            }
        }
        return monitorGuage;
    }

}
