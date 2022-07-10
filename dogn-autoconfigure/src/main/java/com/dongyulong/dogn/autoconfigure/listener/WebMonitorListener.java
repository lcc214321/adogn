package com.dongyulong.dogn.autoconfigure.listener;

import com.dongyulong.dogn.common.config.CommonUtils;
import io.prometheus.client.Counter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 从老系统的兼容过来的代码信息
 **/
@Slf4j
public class WebMonitorListener implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    private ScheduledExecutorService scheduledService;

    private volatile boolean started = false;

    private JarScanner jarScanner;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        jarScanner = new JarScanner();
        startMonitor();
    }

    @Override
    public void destroy() throws Exception {
        synchronized (this) {
            if (started && !scheduledService.isShutdown()) {
                scheduledService.shutdown();
                started = false;
                log.info("jar monitor shutdown！");
            }
        }
    }

    /**
     * 开始埋点监控，设置定时上报任务
     */
    private synchronized void startMonitor() {
        if (started) {
            log.warn("jar monitor already started！");
            return;
        }
        scheduledService = Executors.newScheduledThreadPool(1);
        scheduledService.scheduleAtFixedRate(() -> monitor(), 2, 10, TimeUnit.MINUTES);
        started = true;
        log.info("jar monitor started！");
    }

    private void monitor() {
        try {
            Set<String> jarNames = jarScanner.doScanAllJars();
            for (String jarName : jarNames) {
                String artifactId = jarName.split("-\\d")[0];
                String version;
                // 处理jar包不带版本的情况，例如设置了启动参数： -javaagent:/deployment/apps/ttl-java-agent/current/server-http-agent.jar
                if (artifactId.length() == jarName.length()) {
                    artifactId = jarName.substring(0, jarName.length() - 4);
                    version = "";
                } else {
                    version = jarName.substring(artifactId.length() + 1, jarName.length() - 4);
                }
                MonitorHolder.report(artifactId, version);
//                if (log.isDebugEnabled()) {
//                    log.debug("jar monitor report {}", jarName);
//                }
            }
        } catch (Exception e) {
            log.error("jar monitor report error", e);
        }
    }

    /**
     * 数据上报holder, 实现延迟加载
     */
    private static class MonitorHolder {
        private static final Counter MONITOR = Counter
                .build()
                .name("dependency_package")
                .help("application dependency package and version.")
                .labelNames("host", "app", "artifactId", "version")
                .register();

        private static final String HOST = CommonUtils.getHostName();

        private static final String APP_NAME = CommonUtils.getAppName();

        /**
         * 数据上报
         *
         * @param artifactId
         * @param version
         */
        private static void report(String artifactId, String version) {
            MONITOR.labels(HOST, APP_NAME, artifactId, version).inc();
        }

    }
}
