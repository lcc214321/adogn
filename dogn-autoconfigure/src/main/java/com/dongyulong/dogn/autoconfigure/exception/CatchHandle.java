package com.dongyulong.dogn.autoconfigure.exception;

import com.dongyulong.dogn.core.monitor.Counter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author zhangshaolong
 * @create 2021/11/22
 **/
@Aspect
@Slf4j
public class CatchHandle {

    /** 统计接口请求 **/
    private static final Counter SERVICE_ERROR_COUNT = new Counter();

    private static final Counter SERVICE_COUNT = new Counter();

    /**
     * 服务启动打印请求信息,
     */
    public CatchHandle() {
        //TODO
        try {

        } catch (Exception e) {

        }
    }

    @Around(value = "@annotation(errorCatch)")
    public Object around(ProceedingJoinPoint point,Catch errorCatch) throws Throwable{
        try {
            SERVICE_COUNT.getCount();
            return point.proceed();
        } catch (Throwable e) {

        }
        return null;
    }
}
