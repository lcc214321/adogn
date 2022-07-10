package com.dongyulong.dogn.common.exception;

/**
 * 所有业务异常的基类
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/8 8:23 上午
 * @since v1.0
 */
public class DException extends RuntimeException {
    /**
     * 错误信息描述
     */
    private DognCode dognCode;

    public DException() {
        super();
    }

    public DException(DognCode dognCode) {
        super(dognCode.getMsg());
        this.dognCode = dognCode;
    }


    public DException(DognCode dognCode, Throwable e) {
        super(dognCode.getMsg(), e);
        this.dognCode = dognCode;
    }

    public DException(int code, String msg) {
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
