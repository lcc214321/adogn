package com.dongyulong.dogn.common.result;

import com.dongyulong.dogn.common.exception.SuccessCode;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author zhangshaolong
 * @create 2021/11/19
 **/
public class BaseResult implements Serializable {
    /**
     * 错误码
     */
    @JsonProperty
    private int code;

    /**
     * 错误描述
     */
    @JsonProperty
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean success() {
        return code == SuccessCode.SUCCESS.getCode();
    }

    @Override
    public String toString() {
        return "BaseResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
