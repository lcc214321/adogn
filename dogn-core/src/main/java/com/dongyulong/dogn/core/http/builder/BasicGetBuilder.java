package com.dongyulong.dogn.core.http.builder;

import com.dongyulong.dogn.core.http.HttpClient;
import com.dongyulong.dogn.core.http.RequestClient;
import com.dongyulong.dogn.core.http.request.BasicGetRequest;
import okhttp3.HttpUrl;
import org.apache.commons.collections4.MapUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * get基本请求构造信息
 * @author zhangshaolong
 * @create 2022/1/26
 */
public class BasicGetBuilder extends OkHttpRequestBuilder<BasicGetBuilder> {

    protected Map<String, String> params;

    public BasicGetBuilder(HttpClient httpClient) {
        super(httpClient);
    }

    public BasicGetBuilder params(Map<String, String> params){
        if (MapUtils.isNotEmpty(params)) {
            this.params = params;
        }
        return this;
    }
    
    public BasicGetBuilder addQueryParam(String key, String val){
        if (this.params == null){
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    @Override
    public RequestClient build() {
        return new BasicGetRequest(httpClient, rewriteURIWithParams(url, params), tag, headers).build();
    }

    private String rewriteURIWithParams(String url, Map<String, String> params){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (MapUtils.isNotEmpty(params)){
            for (Map.Entry<String, String> param : params.entrySet()){
                String paramName = param.getKey();
                String paramValue = param.getValue();
                urlBuilder.addQueryParameter(paramName, paramValue);
            }
        }
        return urlBuilder.build().toString();
    }
}
