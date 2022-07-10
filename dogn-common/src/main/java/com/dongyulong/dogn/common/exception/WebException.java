package com.dongyulong.dogn.common.exception;

/**
 * 所有业务异常的基类
 *
 * @author zhangshaolong
 * @create 2021/11/18
 **/

public class WebException extends RuntimeException {
    /**
     * 错误信息描述
     */
    private DognCode dognCode;

    public WebException() {
        super();
    }

    public WebException(DognCode dognCode) {
        super(dognCode.getMsg());
        this.dognCode = dognCode;
    }

    public WebException(DognCode dognCode, Throwable e) {
        super(dognCode.getMsg(), e);
        this.dognCode = dognCode;
    }

    public WebException(int code, String msg) {
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
