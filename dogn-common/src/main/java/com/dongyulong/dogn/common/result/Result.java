package com.dongyulong.dogn.common.result;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author zhangshaolong
 * @create 2021/11/18
 **/
public class Result<T> extends BaseResult implements Serializable {

    /**
     * 具体的返回信息
     */
    @JsonProperty
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "data=" + data +
                '}';
    }
}
