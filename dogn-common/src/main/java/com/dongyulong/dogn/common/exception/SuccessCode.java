package com.dongyulong.dogn.common.exception;

/**
 * 服务处理成功的错误
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/8 3:07 下午
 * @since v1.0
 */
public enum SuccessCode implements DognCode {

    /**
     * 处理成功
     **/
    SUCCESS(0, "success");

    private int code;

    private String message;

    SuccessCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return message;
    }
}
