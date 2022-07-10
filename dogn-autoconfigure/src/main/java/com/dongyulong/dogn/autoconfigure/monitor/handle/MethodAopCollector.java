package com.dongyulong.dogn.autoconfigure.monitor.handle;

import com.dongyulong.dogn.core.method.DognMethod;
import com.dongyulong.dogn.core.method.MethodMonitor;
import com.dongyulong.dogn.core.monitor.DognMonitor;
import com.dongyulong.dogn.core.monitor.indicator.MonitorType;
import com.dongyulong.dogn.autoconfigure.filter.common.StartMonitor;
import com.dongyulong.dogn.autoconfigure.filter.common.TimeUtils;
import org.aspectj.lang.JoinPoint;

/**
 * 方法的aop拦截信息
 *
 * @author zhangshaolong
 * @create 2021/12/21
 **/
public class MethodAopCollector implements AopCollector {
    /**
     * 监控信息
     */
    private StartMonitor startMonitor;

    public MethodAopCollector() {
        startMonitor = new StartMonitor();
        startMonitor.start("method");
    }

    @Override
    public void before(JoinPoint jp, String key) {
        startMonitor.incrementCount(key);
    }

    @Override
    public void afterAop(Object object, JoinPoint jp, String key, long time) {
        getMethodMonitor(key).inc();
        if (null == object) {
            startMonitor.incrementError(key);
            getMethodMonitor(key).recordExeczution(false, TimeUtils.timeCost(time));
        } else {
            getMethodMonitor(key).recordExeczution(true, TimeUtils.timeCost(time));
        }
    }

    ;

    private MethodMonitor getMethodMonitor(String key) {
        DognMethod monitor = (DognMethod) DognMonitor.getInstance().getSelfMonitor(MonitorType.METHOD, key);
        return monitor.getMonitor();
    }

}
