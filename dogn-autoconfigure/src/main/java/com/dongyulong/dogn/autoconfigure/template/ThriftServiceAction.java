package com.dongyulong.dogn.autoconfigure.template;

import com.dongyulong.dogn.common.exception.DException;
import com.dongyulong.dogn.common.exception.DognCode;

/**
 * 接口返回信息,添加check的校验数据信息
 *
 * @author zhangshaolong
 * @create 2022/1/21
 **/
public interface ThriftServiceAction<T> extends ThriftServiceNoCheckAction<T> {

    /**
     * 检查接口的参数信息
     *
     * @return -
     * @throws DException -
     */
    DognCode checkParam() throws DException;
}
