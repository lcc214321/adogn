package com.dongyulong.dogn.autoconfigure.template;

import com.dongyulong.dogn.common.exception.DException;
import com.dongyulong.dogn.common.exception.DognCode;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.common.exception.IgnoreException;
import com.dongyulong.dogn.common.exception.SuccessCode;
import com.dongyulong.dogn.common.exception.WebException;
import com.dongyulong.dogn.common.result.Result;
import com.dongyulong.dogn.common.result.ResultBuilder;
import com.dongyulong.dogn.core.log.LogHelper;
import com.dongyulong.dogn.metrics.spring.InterfaceMonitor;
import com.dongyulong.dogn.autoconfigure.filter.common.TimeUtils;
import com.dongyulong.dogn.autoconfigure.filter.holder.ThriftMethodHolder;
import com.dongyulong.dogn.autoconfigure.monitor.common.AppCommon;
import com.dongyulong.dogn.autoconfigure.monitor.handle.ThriftAopCollector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * 基础的模版类型
 *
 * @author zhangshaolong
 * @create 2022/1/21
 **/
@Slf4j
public class DefaultThriftService implements ThriftServiceTemplate {

    /**
     * 执行业务方法处理返回信息
     *
     * @param action -
     * @param <T>    -
     * @return -
     */
    @Override
    public <T> Result<T> execute(final ThriftServiceAction<T> action) {
        long time = TimeUtils.currentTimeMillis();
        Result<T> result;
        String method = ThriftMethodHolder.get();
        try {
            DognCode errorCode = action.checkParam();
            if (errorCode != SuccessCode.SUCCESS) {
                //处理下一步业务逻辑,参数错误之类的那就直接返回
                errroCode(method, errorCode.getCode(), time);
                return ResultBuilder.buildFailResult(errorCode);
            }
            T data = action.doAction();
            result = ResultBuilder.buildSuccessResult(data);
        } catch (IgnoreException e) {
            result = ResultBuilder.buildFailResult(e.getErrorCode());
            log.error(String.format("%s IgnoreException failed", method), e);
        } catch (DException e) {
            //自定义异常打点信息,给不会统一的返回值信息
            result = getErrorResult(method, e.getErrorCode(), time);
            log.error(String.format("%s DException failed", method), e);
        } catch (WebException e) {
            //自定义异常打点信息,给不会统一的返回值信息
            result = getErrorResult(method, e.getErrorCode(), time);
            log.error(String.format("%s WebException failed", method), e);
        } catch (Throwable e) {
            //服务级别的数据信息
            result = getErrorResult(method, ErrorCode.SERVICE_ERROR, time);
            log.error(String.format("%s failed", method), e);
        } finally {
            //时间打点
            addLog(method, time);
        }
        return result;
    }

    /**
     * 不用检查数据结果信息
     *
     * @param action -
     * @param <T>    -
     * @return -
     */
    @Override
    public <T> Result<T> execute(ThriftServiceNoCheckAction<T> action) {
        long time = TimeUtils.currentTimeMillis();
        Result<T> result;
        String method = ThriftMethodHolder.get();
        try {
            T data = action.doAction();
            result = ResultBuilder.buildSuccessResult(data);
        } catch (IgnoreException e) {
            result = ResultBuilder.buildFailResult(e.getErrorCode());
            log.warn(String.format("%s IgnoreException end warn", method), e);
        } catch (DException e) {
            //自定义异常打点信息,给不会统一的返回值信息
            result = getErrorResult(method, e.getErrorCode(), time);
            log.error(String.format("%s DException failed", method), e);
        } catch (WebException e) {
            //自定义异常打点信息,给不会统一的返回值信息
            result = getErrorResult(method, e.getErrorCode(), time);
            log.error(String.format("%s WebException failed", method), e);
        } catch (Throwable e) {
            //服务级别的数据信息
            result = getErrorResult(method, ErrorCode.SERVICE_ERROR, time);
            log.error(String.format("%s failed", method), e);
        } finally {
            //时间打点
            addLog(method, time);
        }
        return result;
    }


    private <T> Result<T> getErrorResult(String method, DognCode didaCode, long time) {
        errroCode(method, didaCode.getCode(), time);
        return ResultBuilder.buildFailResult(didaCode);
    }

    /**
     * 打点错误信息
     *
     * @param code -
     * @param time -
     */
    private void errroCode(String method, int code, long time) {
        if (StringUtils.isEmpty(method)) {
            log.warn("errroCode method is null");
            return;
        }
        if (code == ErrorCode.SERVICE_ERROR.getCode()) {
            InterfaceMonitor.getInstance().addFail(method, InterfaceMonitor.TYPE_INTERFACE);
            InterfaceMonitor.getInstance().setDuration(method, InterfaceMonitor.TYPE_INTERFACE, TimeUtils.timeCost(time),
                    InterfaceMonitor.ERROR_FAIL);
        }
        if (code != SuccessCode.SUCCESS.getCode()) {
            ThriftAopCollector.moniorError(method);
        }
    }


    /**
     * 打点日志信息
     *
     * @param time -
     */
    private void addLog(String method, long time) {
        if (StringUtils.isEmpty(method)) {
            log.warn("addLog method is null");
            return;
        }
        InterfaceMonitor.getInstance().setDuration(method, InterfaceMonitor.TYPE_INTERFACE, time,
                InterfaceMonitor.ERROR_SUCC);
        long costTime = TimeUtils.timeCost(time);
        if (costTime >= AppCommon.getSlow()) {
            LogHelper.slowLog(method + " (cost " + costTime + " ms)");
        }
        MDC.remove("traceId");
        ThriftMethodHolder.remove();
    }
}
