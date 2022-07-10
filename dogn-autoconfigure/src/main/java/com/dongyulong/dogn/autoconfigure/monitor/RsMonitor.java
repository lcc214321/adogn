package com.dongyulong.dogn.autoconfigure.monitor;

import com.dongyulong.dogn.aurora.core.Aurora;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.common.exception.WebException;
import com.dongyulong.dogn.common.result.BaseResult;
import com.dongyulong.dogn.metrics.spring.InterfaceMonitor;
import com.dongyulong.dogn.autoconfigure.tools.SpringUtils;
import com.dongyulong.dogn.autoconfigure.filter.common.ExcludeUrl;
import com.dongyulong.dogn.autoconfigure.filter.holder.MethodHolder;
import com.dongyulong.dogn.autoconfigure.monitor.handle.AopCollector;
import com.dongyulong.dogn.tools.json.JsonTools;
import com.dongyulong.dogn.tools.system.SystemPropertyUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * rs层服务报警监控信息
 *
 * @author zhangshaolong
 * @create 2021/12/17
 **/
@Aspect
@Slf4j
@Order(-10)
public class RsMonitor {

    private Aurora aurora = SpringUtils.getBean(Aurora.class);
    private static final String DEFAULT_RATE_LIMITED = "{\"code\": 106, \"message\": \"系统繁忙，请稍后重试。\"}";
    private static final String PROPERTY_PREFIX = "aurora.res.limited.";
    private static final Map<String, Object> RATE_LIMITED_RES = Maps.newConcurrentMap();


    private AopCollector aopCollector;

    public RsMonitor(AopCollector aopCollector) {
        this.aopCollector = aopCollector;
        log.info("rsmonitor aop start.....");
    }

    @Pointcut("execution(* com.dongyulong..*.*(..)) && " +
            "(@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
            "||@annotation(org.springframework.web.bind.annotation.PostMapping))" +
            "||@annotation(org.springframework.web.bind.annotation.GetMapping))")
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
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object resultObject = null;
        Object[] args = pjp.getArgs();
        String path = request.getRequestURI();
        boolean result = ExcludeUrl.contain(path);
        if (result) {
            //过滤不需要的url请求拦截信息
            return pjp.proceed(args);
        }
        aopCollector.before(pjp, path);
        Class clazz = pjp.getTarget().getClass();
        try {
            //做了一个单机限流处理
            String resourceName = clazz.getSimpleName() + "::" + pjp.getSignature().getName();
            if (aurora == null) {
                aurora = SpringUtils.getBean(Aurora.class);
            }
            if (aurora.take(resourceName)) {
                //返回的结果信息
                resultObject = pjp.proceed(args);
                aopCollector.after(resultObject, pjp, path, null);
                return resultObject;
            } else {
                //不通过直接返回限流处理结果
                Object limitData = RATE_LIMITED_RES.get(resourceName);
                if (limitData == null) {
                    limitData = SystemPropertyUtils.getProperty(PROPERTY_PREFIX + resourceName, DEFAULT_RATE_LIMITED);
                    RATE_LIMITED_RES.put(resourceName, limitData);
                }
                InterfaceMonitor.getInstance().addWarn(MethodHolder.get(), "fallback");
                //需要处理结果返回值信息
                BaseResult baseResult = JsonTools.toT(limitData.toString(), BaseResult.class);
                if (baseResult == null) {
                    baseResult = new BaseResult();
                    baseResult.setCode(ErrorCode.SERVICE_BUSY.getCode());
                    baseResult.setMessage(ErrorCode.SERVICE_BUSY.getMsg());
                }
                throw new WebException(baseResult.getCode(), baseResult.getMessage());
            }
        } catch (Throwable throwable) {
            aopCollector.after(resultObject, pjp, path, throwable);
            throw throwable;
        }
    }
}
