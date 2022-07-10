package com.dongyulong.dogn.autoconfigure.filter.common;

import com.dongyulong.dogn.core.monitor.Counter;
import com.dongyulong.dogn.autoconfigure.monitor.common.AlarmCommon;
import com.dongyulong.dogn.autoconfigure.monitor.common.AppCommon;
import com.google.common.util.concurrent.AtomicLongMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * @author zhangshaolong
 * @create 2021/12/21
 **/
@Slf4j
public class StartMonitor {


    /**
     * 接口的请求数量
     */
    private AtomicLongMap<String> methodCount = AtomicLongMap.create();

    /**
     * 错误信息数的统计
     */
    private AtomicLongMap<String> methodErrorCount = AtomicLongMap.create();


    /**
     * 错误码信息的统计
     */
    private AtomicLongMap<String> errorCodeCount = AtomicLongMap.create();


    /**
     * 记录线程数信息
     */
    private Counter threadCount = new Counter();


    /**
     * 监控接口信息
     */
    public void start(String name) {
        try {
            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(name + "-monitor-thread-%d").build();
            ExecutorService service = newSingleThreadExecutor(namedThreadFactory);
            service.execute(() -> monitor(name));
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    service.shutdown();
                } catch (Exception e) {
                    log.error("MonitorHandlerInterceptor shutdown exception", e);
                }
            }));
            log.info("start {} monitor.....", name);
        } catch (Exception e) {
            log.error("MonitorHandlerInterceptor error", e);
        }
    }

    /**
     * 监控报警数据
     */
    private void monitor(String monitorName) {
        while (!Thread.interrupted()) {
            sleep();
            try {
                if (log.isDebugEnabled()) {
                    log.debug("{} monitor data......", monitorName);
                }
                monitorData(monitorName);
            } catch (Exception e) {
                log.error("monitor error", e);
            }
        }
    }

    /**
     * 统计数据信息
     */
    private void monitorData(String monitorName) {
        Map<String, Long> countMap = getCurrentCountMap();
        Map<String, Long> errorMap = getCurrentErrorMap();
        if (countMap == null) {
            return;
        }
        for (Map.Entry<String, Long> entry : countMap.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            //有限服务级别的报警
            double errorCount = MapUtils.getDouble(errorMap, key, 0d);
            double percent = errorCount / value;
            //通过服务配置获取报警阈值信息
            if (percent > AppCommon.getAlarm()) {
                //TODO 报警其他服务信息
                log.warn("key:{},count:{},errorCount:{}", key, value, errorCount);
                AlarmCommon.sendAlarm(key, value, (long) errorCount);
            }
        }
        long currentThreadSum = threadCount.getCount();
        if (currentThreadSum > AppCommon.getThread()) {
            String message = "最近" + AppCommon.getMonitor() + "秒内服务的线程数大于>" + AppCommon.getThread() + ",请注意检观察服务耗时";
            AlarmCommon.sendAlarm(message);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(AppCommon.getMonitor() * 1000);
        } catch (Exception e) {
            log.error("sleep error", e);
        }
    }

    private Map<String, Long> getCurrentErrorMap() {
        return getCurrentMap(methodErrorCount);
    }

    private Map<String, Long> getCurrentCountMap() {
        return getCurrentMap(methodCount);
    }


    /**
     * 统计线程的信息
     */
    public void incThread() {
        threadCount.inc();
    }

    /**
     * 减去统计线程的信息
     */
    public void decThread() {
        threadCount.dec();
    }


    /**
     * 请求信息的统计
     */
    public void incrementError(String path) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        methodErrorCount.incrementAndGet(path);
    }


    /**
     * 错误请求信息统计
     */
    public void incrementCount(String path) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        methodCount.incrementAndGet(path);
    }


    private Map<String, Long> getCurrentMap(AtomicLongMap<String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Long> tempMap = new HashMap<>(map.asMap());
        map.clear();
        return tempMap;
    }
}
