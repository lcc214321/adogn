package com.dongyulong.dogn.core.http.request;

import com.dongyulong.dogn.core.http.HttpClient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Map;

/**
 * 基础的get请求信息
 * @author zhangshaolong
 * @create 2022/1/26
 **/
public class BasicGetRequest extends OkHttpRequest {

    private MediaType mediaType;

    public BasicGetRequest(HttpClient httpClient, String url, Object tag, Map<String, String> headers) {
        super(httpClient, url, tag, headers);
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.get().build();
    }

}
