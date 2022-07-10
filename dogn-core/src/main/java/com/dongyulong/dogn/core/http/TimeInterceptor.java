package com.dongyulong.dogn.core.http;


import com.dongyulong.dogn.tools.json.JsonTools;
import com.dongyulong.dogn.tools.system.SystemPropertyUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * http 动态时间
 *
 * @author zhangshaolong
 * @create 2022/2/7
 **/
public class TimeInterceptor implements Interceptor {

    private final static String KEY_PREFIX = "okhttp.";

    private String httpName;

    private String key;

    public TimeInterceptor(String httpName) {
        this.httpName = httpName;
        this.key = KEY_PREFIX + httpName;
    }

    /**
     * 修改时间信息
     *
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpTime httpTime = getHttpTime();
        if (httpTime != null && httpTime.connectTime > 0L) {
            chain = chain.withConnectTimeout(httpTime.connectTime, TimeUnit.MILLISECONDS);
        }
        if (httpTime != null && httpTime.writeTime > 0L) {
            chain = chain.withWriteTimeout(httpTime.writeTime, TimeUnit.MILLISECONDS);
        }
        if (httpTime != null && httpTime.readTime > 0L) {
            chain = chain.withReadTimeout(httpTime.readTime, TimeUnit.MILLISECONDS);
        }
        return chain.proceed(request);
    }


    /**
     * 获取配置信息报警信息
     *
     * @return
     */
    public HttpTime getHttpTime() {
        String data = SystemPropertyUtils.getProperty(key, "");
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        return JsonTools.toT(data, HttpTime.class);
    }

    public static class HttpTime implements Serializable {
        private Integer connectTime;
        private Integer writeTime;
        private Integer readTime;

        public Integer getConnectTime() {
            return connectTime;
        }

        public void setConnectTime(Integer connectTime) {
            this.connectTime = connectTime;
        }

        public Integer getWriteTime() {
            return writeTime;
        }

        public void setWriteTime(Integer writeTime) {
            this.writeTime = writeTime;
        }

        public Integer getReadTime() {
            return readTime;
        }

        public void setReadTime(Integer readTime) {
            this.readTime = readTime;
        }
    }
}
