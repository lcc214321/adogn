package com.dongyulong.dogn.core.http;


import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;

/**
 * 异步回调
 * @author zhangshaolong
 * @create 2022/1/26
 */
public interface Callback extends okhttp3.Callback {

    Callback CALLBACK_DEFAULT = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

        }
    };
}
