package com.dongyulong.dogn.core.http.builder;

import com.dongyulong.dogn.core.http.HttpClient;
import com.dongyulong.dogn.core.http.RequestClient;
import com.dongyulong.dogn.core.http.request.JsonPostRequest;
import okhttp3.MediaType;


/**
 * post的json提交
 * @author zhangshaolong
 * @create 2022/1/26
 */
public class JsonPostBuilder extends OkHttpRequestBuilder<JsonPostBuilder> {

    private String content;
    private MediaType mediaType;

    public JsonPostBuilder(HttpClient httpClient) {
        super(httpClient);
    }

    public JsonPostBuilder content(String json) {
        this.content = json;
        return this;
    }

    public JsonPostBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestClient build() {
        return new JsonPostRequest(httpClient, url, tag, headers, content, mediaType).build();
    }
}
