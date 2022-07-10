package com.dongyulong.dogn.common.exception;

/**
 * toast提示信息
 *
 * @author zhangshaolong
 * @create 2021/11/19
 **/
public class ToastException extends RuntimeException {

    /**
     * 错误信息描述
     */
    private DognCode dognCode;

    private Object toast;

    public ToastException() {
        super();
    }

    public ToastException(DognCode dognCode, Object toast) {
        super(dognCode.getMsg());
        this.dognCode = dognCode;
        this.toast = toast;
    }

    public DognCode getCode() {
        return dognCode;
    }

    public Object getToast() {
        return toast;
    }
}
