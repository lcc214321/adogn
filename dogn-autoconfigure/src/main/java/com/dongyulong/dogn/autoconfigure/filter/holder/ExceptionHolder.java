package com.dongyulong.dogn.autoconfigure.filter.holder;

import com.dongyulong.dogn.common.exception.SuccessCode;

/**
 * 异常信息的捕获打点
 * @author zhangshaolong
 * @create 2021/12/17
 * */
public class ExceptionHolder {

    private static ThreadLocal<ErrorContext> holder =  ThreadLocal.withInitial(ErrorContext::new);

    public static void set(int code,boolean exception) {
        ErrorContext errorContext  = new ErrorContext();
        errorContext.code = code;
        errorContext.exception = exception;
        holder.set(errorContext);
    }

    public static ErrorContext get() {
        return holder.get();
    }

    public static void remove() {
        holder.remove();
    }

    public static class ErrorContext {

        public int code = SuccessCode.SUCCESS.getCode();
        public boolean exception = false;


        /**
         * 请求成功
         * @return
         */
        public boolean success() {
            return !exception && code ==  SuccessCode.SUCCESS.getCode();
        }

        /**
         * 请求失败
         * @return
         */
        public boolean warn() {
            return !exception || (code != 104 && code != 101);
        }

        /**
         * 请求失败
         * @return
         */
        public boolean fail() {
            return !exception || (code == 104 || code == 101);
        }

        /**
         * 请求失败
         * @return
         */
        public boolean logfail() {
            return exception && (code != SuccessCode.SUCCESS.getCode());
        }
    }
}
