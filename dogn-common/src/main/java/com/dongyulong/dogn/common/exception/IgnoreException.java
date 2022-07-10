package com.dongyulong.dogn.common.exception;

/**
 * 忽略的异常，不会计入监控
 *
 * @author dongy
 * @date 17:14 2022/4/20
 **/
public class IgnoreException extends RuntimeException {

    /**
     * 错误信息描述
     */
    private final transient DognCode dognCode;

    public IgnoreException() {
        super();
        dognCode = ErrorCode.SERVICE_ERROR;
    }

    public IgnoreException(DognCode dognCode) {
        super(dognCode.getMsg());
        this.dognCode = dognCode;
    }

    public IgnoreException(DognCode dognCode, Throwable e) {
        super(dognCode.getMsg(), e);
        this.dognCode = dognCode;
    }

    public IgnoreException(int code, String msg) {
        super(msg);
        this.dognCode = new DognCode() {
            @Override
            public int getCode() {
                return code;
            }

            @Override
            public String getMsg() {
                return msg;
            }
        };
    }

    public DognCode getErrorCode() {
        return dognCode;
    }
}
