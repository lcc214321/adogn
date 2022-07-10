package com.dongyulong.dogn.autoconfigure.filter;

import com.dongyulong.dogn.autoconfigure.filter.holder.ExceptionHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 返回格式的处理
 * @author zhang.shaolong
 * @create 2021/12/16
 **/
public class ResponseHeaderFilter extends OncePerRequestFilter {

    public static final String RESPONSE_CODE_HEADER = "X-Biz-Code";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        httpServletResponse.addHeader("Cache-Control", "no-cache='set-cookie'");
        String contentType = httpServletResponse.getHeader("Content-Type");
        if (StringUtils.isEmpty(contentType)) {
            //默认都走json
            httpServletResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        //给客户端的返回信息
        after(httpServletResponse);
    }

    private void after(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader(RESPONSE_CODE_HEADER, String.valueOf(ExceptionHolder.get().code));
    }
}
