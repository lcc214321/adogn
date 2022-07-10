package com.dongyulong.dogn.autoconfigure.filter.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接口的拦截,服务请求之前的处理逻辑
 * @author zhangshaolong
 * @create 2021/12/17
 **/
public class WebHandlerInterceptor extends HandlerInterceptorAdapter {

    /**
     * 请求前的处理逻辑信息
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();
        request.getMethod();
        boolean isHandlerMethod = handler instanceof HandlerMethod;
        if (!isHandlerMethod) {
            return true;
        }
        //处理请求之间的数据信息
        return super.preHandle(request, response, handler);
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
