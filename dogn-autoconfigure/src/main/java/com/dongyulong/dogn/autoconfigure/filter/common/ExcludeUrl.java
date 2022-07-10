package com.dongyulong.dogn.autoconfigure.filter.common;


import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.List;

/**
 * 过滤不用的url信息
 *
 * @author zhang.shaolong
 * @create 2021/12/17
 **/
public class ExcludeUrl {

    private static AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static final List<String> EXCLUDE_PATHS_LIST = Arrays.asList(
            "/error",
            "/metrics",
            "/metrics.*",
            "/api-docs.*",
            "/autoconfig",
            "/configprops",
            "/health",
            "/info",
            "/swagger.*",
            "/dump",
            "/juno",
            "/mappings",
            "/error/**",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js",
            "/**/*.png",
            "/**/*.jpg",
            "/**/*.jpeg",
            "/**/*.gif",
            "/hystrix.stream",
            "/favicon.ico"
    );

    public static boolean contain(String url) {
        return ExcludeUrl.EXCLUDE_PATHS_LIST.stream().anyMatch(pattern -> antPathMatcher.match(pattern, url));
    }
}
