package com.dongyulong.dogn.core.http.request;

import com.dongyulong.dogn.core.http.HttpClient;
import com.dongyulong.dogn.core.http.RequestClient;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Map;

/**
 * 构造基础的服务数据
 * @author zhangshaolong
 * @create 2022/1/26
 **/
public abstract class OkHttpRequest {

    /**
     * 请求url
     */
    protected String url;
    protected Object tag;
    protected Map<String, String> headers;
    protected HttpClient httpClient;
    protected Request.Builder builder = new Request.Builder();

    protected OkHttpRequest(HttpClient httpClient, String url, Object tag, Map<String, String> headers) {
        this.httpClient = httpClient;
        this.url = url;
        this.tag = tag;
        this.headers = headers;
        initBuilder();
    }

    private void initBuilder() {
        builder.url(url).tag(tag);
        appendHeaders();
    }

    protected void appendHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) {
            return;
        }
        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }

    public RequestClient build() {
        return new RequestClient(this);
    }

    public Request generateRequest() {
        RequestBody requestBody = buildRequestBody();
        Request request = buildRequest(requestBody);
        return request;
    }

    public HttpClient getHttpClient(){
        return httpClient;
    }

    /**
     * 获取请求的数据
     * @return
     */
    protected abstract RequestBody buildRequestBody();

    /**
     * 获取请求的request信息
     *
     * @param requestBody
     * @return
     */
    protected abstract Request buildRequest(RequestBody requestBody);
    
}
