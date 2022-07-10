package com.dongyulong.dogn.common.result;

import com.dongyulong.dogn.common.exception.DognCode;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.common.exception.SuccessCode;

/**
 * 构造返回具体的服务信息,对外层的spring rest服务使用
 *
 * @author zhangshaolong
 * @create 2021/11/18
 **/
public final class ResultBuilder {

    /**
     * 返回指定错误码信息
     *
     * @return
     */
    public static <T> Result<T> buildFailResult() {
        return buildFailResult(ErrorCode.SERVICE_ERROR);
    }

    /**
     * 返回指定错误码信息
     *
     * @param dognCode
     * @return
     */
    public static BaseResult buildFail(DognCode dognCode) {
        BaseResult baseResult = new BaseResult();
        baseResult.setCode(dognCode.getCode());
        baseResult.setMessage(dognCode.getMsg());
        return baseResult;
    }

    /**
     * 返回指定错误码信息
     *
     * @param message
     * @return
     */
    public static BaseResult buildFail(String message, int code) {
        BaseResult baseResult = new BaseResult();
        baseResult.setCode(code);
        baseResult.setMessage(message);
        return baseResult;
    }

    /**
     * 返回指定业务错误码信息
     *
     * @param dognCode
     * @return
     */
    public static <T> Result<T> buildFailResult(DognCode dognCode) {
        return buildFailResult(dognCode.getCode(), dognCode.getMsg());
    }

    /**
     * 返回指定错误码信息
     *
     * @param code
     * @param msg
     * @return
     */
    public static <T> Result<T> buildFailResult(int code, String msg) {
        return buildResult(code, msg, null);
    }

    /**
     * 返回成功的信息
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> buildSuccessResult(T data) {
        return buildResult(SuccessCode.SUCCESS.getCode(), SuccessCode.SUCCESS.getMsg(), data);
    }

    public static <T> Result<T> buildResult(int code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(msg);
        result.setData(data);
        return result;
    }

}
