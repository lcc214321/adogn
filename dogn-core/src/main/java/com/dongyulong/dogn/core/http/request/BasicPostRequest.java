package com.dongyulong.dogn.core.http.request;

import com.dongyulong.dogn.core.http.HttpClient;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.collections4.MapUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * post的from表单请求
 * @author zhangshaolong
 * @create 2022/1/26
 */
public class BasicPostRequest extends OkHttpRequest {

    protected Map<String, String> fieldParams;
    private Charset charset;

    public BasicPostRequest(HttpClient httpClient, String url, Object tag, Map<String, String> fieldParams, Map<String, String> headers, Charset charset) {
        super(httpClient, url, tag, headers);
        this.fieldParams = fieldParams;
        if (charset == null) {
            this.charset = StandardCharsets.UTF_8;
        }
    }

    @Override
    protected RequestBody buildRequestBody() {
        FormBody.Builder builder = new FormBody.Builder(charset);
        if (MapUtils.isNotEmpty(fieldParams)) {
            fieldParams.forEach(builder::add);
        }
        return builder.build();
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.post(requestBody).build();
    }
}
