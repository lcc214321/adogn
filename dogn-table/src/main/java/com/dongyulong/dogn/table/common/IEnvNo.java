package com.dongyulong.dogn.table.common;

/**
 * 部署环境
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/810:11 上午
 * @since v1.0
 */
public interface IEnvNo {

    /**
     * 获取环境标识key
     *
     * @return -
     */
    String getEnvKey();

    /**
     * 获取环境编号
     * 0～9
     *
     * @return -
     */
    int getNo();

}
