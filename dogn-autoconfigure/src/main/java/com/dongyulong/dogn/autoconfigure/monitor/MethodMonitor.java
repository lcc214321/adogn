package com.dongyulong.dogn.autoconfigure.monitor;

import com.dongyulong.dogn.autoconfigure.monitor.handle.AopCollector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 统一埋点业务的处理信息
 *
 * @author zhangshaolong
 * @create 2021/11/22
 **/
@Aspect
@Slf4j
public class MethodMonitor {

    private AopCollector aopCollector;

    public MethodMonitor(AopCollector aopCollector) {
        this.aopCollector = aopCollector;
        log.info("methodmonitor aop start.....");
    }

    /**
     * 服务启动打印请求信息,
     */
    @Around(value = "@annotation(monitor)")
    public Object around(ProceedingJoinPoint point, Monitor monitor) throws Throwable {
        Object result = null;
        String monitorKey = getMonitorKey(point, monitor);
        long startTime = System.currentTimeMillis();
        if (log.isDebugEnabled()) {
            log.debug("method monitor method:{}", monitorKey);
        }
        try {
            aopCollector.before(point, monitorKey);
            result = point.proceed();
            return result;
        } finally {
            aopCollector.afterAop(result, point, monitorKey, startTime);
        }
    }


    /**
     * 默认是方法名称key
     *
     * @param monitor
     * @return
     */
    private String getMonitorKey(ProceedingJoinPoint point, Monitor monitor) {
        String monitorKey = monitor.value();
        if (StringUtils.isEmpty(monitorKey)) {
            String simpleName = point.getTarget().getClass().getSimpleName();
            monitorKey = simpleName + "_" + point.getSignature().getName();
        }
        return monitorKey;
    }


}
