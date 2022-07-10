package com.dongyulong.dogn.autoconfigure.log;

import com.dongyulong.dogn.core.annotation.LogOpen;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;


/**
 * 日志切面
 *
 * @author dongy
 * @date 11:42 2022/2/9
 **/
@Aspect
public class MethodLogAspect {

    private final MethodLogAroundHandler methodLogAroundHandler = new MethodLogAroundHandler();

    @Around(value = "@within(logOpen) || @annotation(logOpen)")
    public Object around(final ProceedingJoinPoint pjp, LogOpen logOpen) throws Throwable {
        if (logOpen != null && !logOpen.open()) {
            return pjp.proceed(pjp.getArgs());
        }
        return methodLogAroundHandler.around(pjp);
    }

}
