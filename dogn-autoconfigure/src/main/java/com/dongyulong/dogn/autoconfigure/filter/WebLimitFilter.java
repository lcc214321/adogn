package com.dongyulong.dogn.autoconfigure.filter;

import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.common.exception.WebException;
import com.dongyulong.dogn.autoconfigure.filter.flowcontrol.RedisRateLimiter;
import com.dongyulong.dogn.autoconfigure.filter.sandbox.E2ETestManager;
import com.dongyulong.dogn.autoconfigure.filter.sandbox.SandboxConstants;
import com.dongyulong.dogn.autoconfigure.monitor.handle.DdcHelper;
import com.dongyulong.dogn.tools.json.JsonTools;
import io.prometheus.client.Counter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 限流的拦截器
 *
 * @author zhangshaolong
 * @create 2021/12/17
 **/
@Slf4j
public class WebLimitFilter extends OncePerRequestFilter {

    private final RedisRateLimiter rateLimiter;

    public WebLimitFilter(RedisRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public final static Counter COUNTER = Counter.build().name("hotparams_flowcontrol_counter")
            .help("Hot params flow control counter.").labelNames("uri", "ip", "cid").register();

    public static final String URI_BEST_MATCH_PATTERN = "org.springframework.web.servlet.HandlerMapping.bestMatchingPattern";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //TODO 限流的处理信息
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (SandboxConstants.E2E_TEST_KEY.equals(req.getHeader(SandboxConstants.FLOW_IDENTITY_NAME))) {
            E2ETestManager.start();
        } else {
            E2ETestManager.reset();
        }
        String resource = req.getAttribute(URI_BEST_MATCH_PATTERN) != null ?
                (String) req.getAttribute(URI_BEST_MATCH_PATTERN) : req.getRequestURI();
        String cid = req.getParameterMap().get("user_cid") == null ? null : req.getParameterMap().get("user_cid")[0];
        String ip = req.getHeader("X-Forwarded-For");
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        if (rateLimiter.tryAcquire(resource, ip) && rateLimiter.tryAcquire(resource, cid)) {
            //通过
            filterChain.doFilter(request, response);
        } else {
            COUNTER.labels(resource, StringUtils.EMPTY, StringUtils.EMPTY).inc();
            log.warn("request:{},client:{} limit!", request.getRequestURI(), JsonTools.toJSON(DdcHelper.getCurrentDdcInfo()));
            throw new WebException(ErrorCode.REQUEST_MORE);
        }
    }
}
