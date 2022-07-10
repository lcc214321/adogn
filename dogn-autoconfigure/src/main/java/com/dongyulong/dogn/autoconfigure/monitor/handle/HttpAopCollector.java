package com.dongyulong.dogn.autoconfigure.monitor.handle;

import com.dongyulong.dogn.common.exception.SuccessCode;
import com.dongyulong.dogn.common.result.Result;
import com.dongyulong.dogn.autoconfigure.filter.common.StartMonitor;
import com.dongyulong.dogn.autoconfigure.filter.holder.ExceptionHolder;
import com.dongyulong.dogn.autoconfigure.filter.holder.MethodHolder;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

/**
 * 处理rs拦截器逻辑
 *
 * @author zhangshaolong
 * @create 2021/12/21
 **/
@Slf4j
public class HttpAopCollector implements AopCollector {

    /**
     * 监控信息
     */
    private StartMonitor startMonitor;

    public HttpAopCollector() {
        startMonitor = new StartMonitor();
        startMonitor.start("http");
    }

    @Override
    public void before(JoinPoint jp, String key) {
        Class clazz = jp.getTarget().getClass();
        String methodName = String.format("%s.%s", clazz.getSimpleName(), jp.getSignature().getName());
        MethodHolder.put(methodName);
        if (log.isDebugEnabled()) {
            log.debug("befer http:{},method:{}", key, methodName);
        }
        startMonitor.incThread();
        startMonitor.incrementCount(key);
    }

    /**
     * 将不是异常的代码也处理下
     *
     * @param result
     * @param jp
     */
    @Override
    public void after(Object result, JoinPoint jp, String key, Throwable throwable) {
        if (log.isDebugEnabled()) {
            log.debug("after http:{},method:{}", key, MethodHolder.get());
        }
        startMonitor.decThread();
        if (null == result || throwable != null) {
            startMonitor.incrementError(key);
            Object[] args = jp.getArgs();
            log.error(String.format("path:%s,name:%s,version:%s os:%s args:%s", key, MethodHolder.get(),
                    DdcHelper.getCurrentDdcInfo() == null ? "null" : DdcHelper.getCurrentDdcInfo().getVersion(),
                    DdcHelper.getCurrentDdcInfo() == null ? "null" : DdcHelper.getCurrentDdcInfo().getOs(),
                    Joiner.on(",").skipNulls().join(args)), throwable);
            return;
        }
        //web接口正常的返回Result格式的信息
        if (result instanceof Result) {
            Result data = (Result) result;
            if (data.getCode() != SuccessCode.SUCCESS.getCode()) {
                //TODO自定义的打印日志 数据信息
                Object[] args = jp.getArgs();
                log.error(String.format("path:%s,name:%s,version:%s os:%s args:%s", key, MethodHolder.get(),
                        DdcHelper.getCurrentDdcInfo() == null ? "null" : DdcHelper.getCurrentDdcInfo().getVersion(),
                        DdcHelper.getCurrentDdcInfo() == null ? "null" : DdcHelper.getCurrentDdcInfo().getOs(),
                        Joiner.on(",").skipNulls().join(args)));
                startMonitor.incrementError(key);
            }
            ExceptionHolder.set(data.getCode(), false);
        } else {
            ExceptionHolder.set(SuccessCode.SUCCESS.getCode(), false);
        }
    }

}
