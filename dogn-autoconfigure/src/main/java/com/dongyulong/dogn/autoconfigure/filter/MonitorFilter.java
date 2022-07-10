package com.dongyulong.dogn.autoconfigure.filter;

import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.core.log.LogHelper;
import com.dongyulong.dogn.metrics.spring.InterfaceMonitor;
import com.dongyulong.dogn.autoconfigure.filter.common.ExcludeUrl;
import com.dongyulong.dogn.autoconfigure.filter.common.TimeUtils;
import com.dongyulong.dogn.autoconfigure.filter.holder.ExceptionHolder;
import com.dongyulong.dogn.autoconfigure.filter.holder.MethodHolder;
import com.dongyulong.dogn.autoconfigure.filter.holder.TimeHolder;
import com.dongyulong.dogn.autoconfigure.monitor.common.AppCommon;
import io.prometheus.client.Counter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;


/**
 * 监控的filter
 *
 * @author zhangshaolong
 * @create 2021/12/17
 **/
@Slf4j
public class MonitorFilter extends OncePerRequestFilter {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public final static Counter CODE_COUNTER = Counter.build().name("http_server_response_code_count")
            .help("Http server response code counter.").labelNames("uri", "code", "method").register();

    public static final String URI_BEST_MATCH_PATTERN= "org.springframework.web.servlet.HandlerMapping.bestMatchingPattern";

    //需要知道服务报错信息,需要知道服务日志信息

    /**
     * 处理请求信息
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("monitor filer url:{} ", request.getRequestURI());
            }
            before(request);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            //服务框架级别的错误返回通用的请求信息
            log.error("doFilterInternal error", e);
            request.setAttribute("filter.error", e);
            ExceptionHolder.set(ErrorCode.SERVICE_ERROR.getCode(), true);
        } finally {
            after(request, response);
        }
        if (request.getAttribute("filter.error") != null) {
            request.getRequestDispatcher("/error/filter").forward(request, response);
            return;
        }
    }

    /**
     * 之前的处理
     * @param request
     */
    private void before(HttpServletRequest request) {
        try {
            //log埋点日志请求，用户追踪一个请求下的日志信息
            String traceId = UUID.randomUUID().toString().replaceAll("-", "");
            MDC.put("traceId", traceId);
            //开启监控打点
            String url = request.getRequestURI();
            boolean result = ExcludeUrl.contain(url);
            if (result) {
                return;
            }
            //设置时间
            TimeHolder.put(TimeUtils.currentTimeMillis());
        } catch (Exception e) {
            log.error("preHandle error", e);
        }
    }

    /**
     * 之后的处理
     * @param request
     * @param response
     */
    private void after(HttpServletRequest request, HttpServletResponse response) {
        String resource = request.getAttribute(URI_BEST_MATCH_PATTERN) != null ?
                (String)request.getAttribute(URI_BEST_MATCH_PATTERN) : request.getRequestURI();
        try {
            //结束打点
            String url = request.getRequestURI();
            boolean result = ExcludeUrl.contain(url);
            if (result) {
                return;
            }
            ExceptionHolder.ErrorContext ex =  ExceptionHolder.get();
            String methodName = MethodHolder.get();
            if (StringUtils.isNotEmpty(methodName)) {
                InterfaceMonitor.getInstance().addTotal(methodName, InterfaceMonitor.TYPE_INTERFACE);
            }
            CODE_COUNTER.labels(resource, String.valueOf(ex.code), request.getMethod()).inc();
            if (ex.success() && StringUtils.isNotEmpty(methodName)) {
                InterfaceMonitor.getInstance().setDuration(methodName, InterfaceMonitor.TYPE_INTERFACE, TimeUtils.timeCost(TimeHolder.get()), InterfaceMonitor.ERROR_SUCC);
                return;
            }
            if (ex.fail() && StringUtils.isNotEmpty(methodName)) {
                InterfaceMonitor.getInstance().addFail(methodName, InterfaceMonitor.TYPE_INTERFACE);
                InterfaceMonitor.getInstance().setDuration(methodName, InterfaceMonitor.TYPE_INTERFACE, TimeUtils.timeCost(TimeHolder.get()), InterfaceMonitor.ERROR_FAIL);
                return;
            }
            //处理服务级别的异常信息
            if (ex.warn() && StringUtils.isNotEmpty(methodName)) {
                InterfaceMonitor.getInstance().addWarn(methodName, InterfaceMonitor.TYPE_INTERFACE);
                if (ex.logfail()) {
                    InterfaceMonitor.getInstance().setDuration(methodName, InterfaceMonitor.TYPE_INTERFACE, TimeUtils.timeCost(TimeHolder.get()), InterfaceMonitor.ERROR_FAIL);
                } else {
                    InterfaceMonitor.getInstance().setDuration(methodName, InterfaceMonitor.TYPE_INTERFACE, TimeUtils.timeCost(TimeHolder.get()), InterfaceMonitor.ERROR_SUCC);
                }
            }
        } catch (Exception e) {
            log.error("afterCompletion error", e);
        } finally {
            long cost = TimeUtils.timeCost(TimeHolder.get());
            if ( cost > AppCommon.getSlow() && StringUtils.isNotEmpty(MethodHolder.get())) {
                InterfaceMonitor.getInstance().addSlow(MethodHolder.get(), InterfaceMonitor.TYPE_INTERFACE);
                //打印慢日志信息
                LogHelper.slowLog(MethodHolder.get() + " (cost " + cost + " ms)------------");
            }
            //TODO 计算中为数时间统计信息
            ExceptionHolder.remove();
            TimeHolder.remove();
            MethodHolder.remove();
            MDC.remove("traceId");
        }
    }


}
