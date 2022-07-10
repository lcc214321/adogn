package com.dongyulong.dogn.autoconfigure.monitor;

import com.dongyulong.dogn.autoconfigure.filter.holder.ThriftMethodHolder;
import com.dongyulong.dogn.autoconfigure.monitor.handle.AopCollector;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

/**
 * thrift服务的拦截信息
 *
 * @author zhangshaolong
 * @create 2021/12/30
 **/
@Aspect
@Slf4j
@Order(-1)
public class ThriftMonitor {

    private AopCollector aopCollector;

    public ThriftMonitor(AopCollector aopCollector) {
        this.aopCollector = aopCollector;
        log.info("thriftmonitor aop start.....");

    }

    /**
     * 只拦截接口后缀为ServiceImpl的接口方法
     */
    @Pointcut("execution (* com.dongyulong..*ServiceImpl.*(..))")
    public void requestMapping() {
    }


    /**
     * 主要做日志的打点统计信息,记录以前的日志统计
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around(value = "requestMapping()")
    public Object handleControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        Class clazz = pjp.getTarget().getClass();
        String methodName = String.format("%s.%s", clazz.getSimpleName(), pjp.getSignature().getName());
        ThriftMethodHolder.put(methodName);
        aopCollector.before(pjp, methodName);
        Object resultObject = null;
        Object[] args = pjp.getArgs();
        try {
            //返回的结果信息
            resultObject = pjp.proceed(args);
            return resultObject;
        } catch (Throwable e) {
            throw e;
        } finally {
            aopCollector.after(resultObject, pjp, methodName, null);
        }
    }
}
