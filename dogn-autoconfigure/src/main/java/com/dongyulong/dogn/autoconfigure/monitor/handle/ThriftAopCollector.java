package com.dongyulong.dogn.autoconfigure.monitor.handle;

import com.dongyulong.dogn.autoconfigure.filter.common.StartMonitor;
import com.dongyulong.dogn.autoconfigure.filter.holder.ThriftMethodHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * thrift服务的拦截
 *
 * @author zhangshaolong
 * @create 2021/12/30
 **/
@Slf4j
public class ThriftAopCollector implements AopCollector {

    /**
     * 监控信息
     */
    private static StartMonitor startMonitor = new StartMonitor();

    public ThriftAopCollector() {
        startMonitor.start("thrift");
    }

    @Override
    public void before(JoinPoint jp, String key) {
//        InterfaceMonitor.getInstance().addTotal(key, InterfaceMonitor.TYPE_INTERFACE);
        String traceId = UUID.randomUUID().toString().replaceAll("-", "");
        MDC.put("traceId", traceId);
        startMonitor.incrementCount(key);
        startMonitor.incThread();
        if (log.isDebugEnabled()) {
            log.debug("before thrift method:{}", key);
        }
    }

    /**
     * 正常的业务逻辑处理
     *
     * @param jp
     */
    @Override
    public void after(Object object, JoinPoint jp, String key, Throwable throwable) {
        if (null == object) {
            moniorError(key);
        }
        startMonitor.decThread();
    }

    /**
     * 添加错误的数据信息
     */
    public static void moniorError(String key) {
        startMonitor.incrementError(key);
        if (log.isDebugEnabled()) {
            log.debug("after thrift error method:{}", ThriftMethodHolder.get());
        }
    }

}
