package com.dongyulong.dogn.core.http.request;

import com.dongyulong.dogn.core.http.HttpClient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Map;

/**
 * post的json request
 * @author zhangshaolong
 * @create 2022/1/26
 */
public class JsonPostRequest<T> extends OkHttpRequest {

    private static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";
    private static MediaType MEDIA_TYPE_APPLICATION_JSON = MediaType.parse(APPLICATION_JSON_UTF8_VALUE);

    private String content;

    private MediaType mediaType;

    public JsonPostRequest(HttpClient httpClient, String url, Object tag, Map<String, String> headers, String content, MediaType mediaType) {
        super(httpClient, url, tag, headers);
        this.content = content;
        this.mediaType = mediaType;
        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_APPLICATION_JSON;
        }
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mediaType, content);
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.post(requestBody).build();
    }
}
