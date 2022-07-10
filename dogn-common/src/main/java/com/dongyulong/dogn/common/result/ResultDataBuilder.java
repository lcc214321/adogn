package com.dongyulong.dogn.common.result;

import com.dongyulong.dogn.common.exception.DognCode;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.common.exception.SuccessCode;
import com.dongyulong.dogn.tools.json.JsonTools;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangshaolong
 * @create 2021/12/24
 **/
public class ResultDataBuilder {

    private final static String CODE = "code";

    private final static String MSG = "msg";

    private final static String DATA = "data";


    public static Builder newSuccessBuilder() {
        return new Builder(SuccessCode.SUCCESS);
    }

    public static Builder newFailBuilder() {
        return new Builder(ErrorCode.SERVICE_ERROR);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Map<String, Object> data = new HashMap<>();

        public Builder() {
        }

        public Builder(DognCode dognCode) {
            data.put(CODE, dognCode.getCode());
            data.put(MSG, dognCode.getMsg());
        }

        public Builder setCode(DognCode dognCode) {
            data.put(CODE, dognCode.getCode());
            data.put(MSG, dognCode.getMsg());
            return this;
        }

        public Builder setCode(int code) {
            data.put(CODE, code);
            return this;
        }

        public Builder setMsg(String msg) {
            data.put(MSG, msg);
            return this;
        }

        public Builder setCodeMsg(int code, String msg) {
            data.put(CODE, msg);
            data.put(MSG, msg);
            return this;
        }

        public Builder put(String key, Object obj) {
            data.put(key, obj);
            return this;
        }

        public Builder data(Object ob) {
            data.put(DATA, ob);
            return this;
        }

        /**
         * 返回结果信息
         *
         * @return
         */
        public Map<String, Object> build() {
            return data;
        }

        public int getCode() {
            return data.get(CODE) == null ? -1 : (Integer) data.get(CODE);
        }

        public String getMsg() {
            return data.get(MSG) == null ? "" : (String) data.get(MSG);
        }


        /**
         * 序列化结果
         *
         * @return
         */
        @Override
        public String toString() {
            return JsonTools.toJSON(data);
        }

    }
}
