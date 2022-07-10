package com.dongyulong.dogn.core.http.builder;


import com.dongyulong.dogn.core.http.HttpClient;
import com.dongyulong.dogn.core.http.RequestClient;
import com.dongyulong.dogn.core.http.request.BasicPostRequest;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * post的from表单提交信息
 *
 * @author zhangshaolong
 * @create 2022/1/26
 */
public class BasicPostBuilder extends OkHttpRequestBuilder<BasicPostBuilder> {

    private  Map<String, String> fieldParams;
    private Charset charset;

    public BasicPostBuilder(HttpClient httpClient) {
        super(httpClient);
    }

    public BasicPostBuilder addFieldParam(String fieldName, String fieldValue){
        if (fieldParams == null){
            fieldParams = new LinkedHashMap<>();
        }
        fieldParams.put(fieldName, fieldValue);
        return this;
    }

    public BasicPostBuilder charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    @Override
    public RequestClient build() {
        return new BasicPostRequest(httpClient, url, tag,fieldParams, headers,charset).build();
    }
}
