package com.dongyulong.dogn.autoconfigure.template;

import com.dongyulong.dogn.common.result.Result;

/**
 * thrift 处理服务模版信息
 *
 * @author zhangshaolong
 * @create 2022/1/21
 **/
public interface ThriftServiceTemplate {

    /**
     * 处理返回信息
     *
     * @param tThriftServiceAction -
     * @return -
     */
    <T> Result<T> execute(ThriftServiceAction<T> tThriftServiceAction);


    /**
     * 处理返回信息,不用检查check信息
     *
     * @param tThriftServiceAction -
     * @return -
     */
    <T> Result<T> execute(ThriftServiceNoCheckAction<T> tThriftServiceAction);

}
