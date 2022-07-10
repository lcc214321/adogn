package com.dongyulong.dogn.core.http.builder;


import com.dongyulong.dogn.core.http.HttpClient;
import com.dongyulong.dogn.core.http.RequestClient;
import org.apache.commons.collections4.MapUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * build信息
 * @author zhangshaolong
 * @create 2022/1/26
 */
public abstract class OkHttpRequestBuilder<T extends OkHttpRequestBuilder> {

    protected String url;

    protected Object tag;

    protected Map<String, String> headers;

    protected HttpClient httpClient;

    public OkHttpRequestBuilder(HttpClient httpClient){
        this.httpClient = httpClient;
    }

    public T url(String url){
        this.url = url;
        return (T) this;
    }

    public T tag(Object tag){
        this.tag = tag;
        return (T)this;
    }

    public T headers(Map<String, String> headers) {
        if (MapUtils.isNotEmpty(headers)) {
            this.headers = headers;
        }
        return (T)this;
    }

    public T addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return (T)this;
    }

    public abstract RequestClient build();
}
