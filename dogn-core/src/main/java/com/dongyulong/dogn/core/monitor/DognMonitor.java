package com.dongyulong.dogn.core.monitor;

import com.dongyulong.dogn.core.executor.DognThreadFactory;
import com.dongyulong.dogn.core.monitor.indicator.IndicatorCollector;
import com.dongyulong.dogn.core.monitor.indicator.MonitorType;
import com.dongyulong.dogn.core.monitor.manager.MonitorGuage;
import com.dongyulong.dogn.core.monitor.manager.MonitorManager;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 监控工厂类
 *
 * @author zhangshaolong
 * @create 2021/12/15
 **/
@Slf4j
public class DognMonitor {

    private static final DognMonitor INSTANCE = new DognMonitor();

    public static DognMonitor getInstance() {
        return INSTANCE;
    }

    private final ConcurrentHashMap<String, Monitor> monitors = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Monitor> selfMonitors = new ConcurrentHashMap<>();


    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new DognThreadFactory("agaue-monitor"));

    private final static ReentrantLock reentrantLock = new ReentrantLock();


    private DognMonitor() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                commitMonitor();
            } catch (Throwable t) {
                log.error("schedule error", t);
            }
        }, 10, 60, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                scheduledExecutorService.shutdown();
                scheduledExecutorService.awaitTermination(5000L, TimeUnit.MILLISECONDS);
                scheduledExecutorService.shutdownNow();
            } catch (Exception e) {
                log.error("agaueMonitor executor shutdown error", e);
            }
            log.info("agaueMonitor executor shutdown");
        }));
    }

    /**
     * 注册监控服务
     *
     * @param monitor
     */
    public void register(Monitor monitor) {
        if (StringUtils.isEmpty(monitor.monitorName())) {
            return;
        }
        String componentInfo = Joiner.on("_").join(monitor.monitorType().getMonitor(), monitor.monitorName());
        final ReentrantLock lock = reentrantLock;
        lock.lock();
        try {
            Monitor pre = monitors.putIfAbsent(componentInfo, monitor);
            if (pre == null) {
                monitor.initMonitor();
                log.info("monitor registered:{} success", componentInfo);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 注册监控服务
     *
     * @param monitor
     */
    public void putNotRegister(Monitor monitor) {
        if (StringUtils.isEmpty(monitor.monitorName())) {
            return;
        }
        String componentInfo = Joiner.on("_").join(monitor.monitorType().getMonitor(), monitor.monitorName());
        final ReentrantLock lock = reentrantLock;
        lock.lock();
        try {
            Monitor pre = selfMonitors.putIfAbsent(componentInfo, monitor);
            if (pre == null) {
                log.info("putNotRegister monitor;{}....", monitor.monitorName());
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * 获取服务信息
     *
     * @param monitType
     */
    public Monitor getMonitor(MonitorType monitType, String monitorName) {
        String componentInfo = Joiner.on("_").join(monitType.getMonitor(), monitorName);
        return monitors.get(componentInfo);
    }

    /**
     * 获取服务信息
     *
     * @param monitType
     */
    public Monitor getSelfMonitor(MonitorType monitType, String monitorName) {
        String componentInfo = Joiner.on("_").join(monitType.getMonitor(), monitorName);
        return selfMonitors.get(componentInfo);
    }

    /**
     * 提交监控数据
     */
    private void commitMonitor() {
        if (log.isDebugEnabled()) {
            log.debug("start report monitor....");
        }
        for (Monitor monitor : monitors.values()) {
            try {
                commitComponent(monitor);
            } catch (Exception e) {
                log.error("process error: {} {}", monitor.monitorType(), monitor.monitorName(), e);
            }
        }
    }

    private void commitComponent(Monitor monitor) {
        IndicatorCollector collector = new IndicatorCollector();
        monitor.report(collector);
        //打点数据
        MonitorGuage monitorGuage = MonitorManager.getMonitor(monitor);
        monitorGuage.commitBySecond(collector);
    }

}
