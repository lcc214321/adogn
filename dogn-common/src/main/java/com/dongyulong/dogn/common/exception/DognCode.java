package com.dongyulong.dogn.common.exception;

/**
 * 错误异常的统一接口
 *
 * @version v1.0
 * @date 2022/7/8 8:24 上午
 * @since v1.0
 */
public interface DognCode {

    /**
     * 获取错误码信息
     *
     * @return -
     */
    int getCode();


    /**
     * 获取错误描述
     *
     * @return -
     */
    String getMsg();

}
