package com.dongyulong.dogn.common.exception;

/**
 * 系统错误信息
 *
 * @author zhangshaolong
 * @create 2021/12/21
 **/
public class SystemException extends RuntimeException {
    /**
     * 错误信息描述
     */
    private DognCode dognCode;

    public SystemException() {
        super();
    }

    public SystemException(ErrorCode dognCode) {
        super(dognCode.getMsg());
        this.dognCode = dognCode;
    }

    public SystemException(ErrorCode dognCode, Throwable e) {
        super(dognCode.getMsg(), e);
        this.dognCode = dognCode;
    }

    public SystemException(int code, String msg) {
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
