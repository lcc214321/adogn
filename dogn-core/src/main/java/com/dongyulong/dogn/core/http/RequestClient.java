package com.dongyulong.dogn.core.http;

import com.dongyulong.dogn.core.http.request.OkHttpRequest;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author zhangshaolong
 * @create 2022/1/26
 */
public class RequestClient {

    private OkHttpRequest okHttpRequest;

    private Request request;

    private Call call;

    public RequestClient(OkHttpRequest request) {
        this.okHttpRequest = request;
    }

    public Call buildCall() {
        request = generateRequest();
        call = okHttpRequest.getHttpClient().getOkHttpClient().newCall(request);
        return call;
    }

    private Request generateRequest() {
        return okHttpRequest.generateRequest();
    }

    /**
     * 同步调用
     *
     * @return
     * @throws IOException
     */
    public Response execute() throws IOException {
        buildCall();
        return getCall().execute();
    }

    /**
     * 异步返回
     *
     * @param callback
     */
    public void execute(Callback callback) {
        buildCall();
        final Callback finalCallback = callback;
        getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                finalCallback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                finalCallback.onResponse(call, response);
            }
        });
    }

    public Call getCall() {
        return call;
    }

    public Request getRequest() {
        return request;
    }

    public OkHttpRequest getOkHttpRequest() {
        return okHttpRequest;
    }


    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

}
