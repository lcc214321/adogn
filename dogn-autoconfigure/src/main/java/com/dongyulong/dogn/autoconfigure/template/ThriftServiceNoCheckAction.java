package com.dongyulong.dogn.autoconfigure.template;


/**
 * 接口返回信息
 *
 * @author zhangshaolong
 * @create 2022/1/21
 **/
public interface ThriftServiceNoCheckAction<T> {

    /**
     * 处理返回信息
     *
     * @return -
     */
    T doAction();
}
