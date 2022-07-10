package com.dongyulong.dogn.table.common;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/88:35 上午
 * @since v1.0
 */
public interface IConfig<T> {

    /**
     * 获取配置key
     *
     * @return -
     */
    String getKey();

    /**
     * 获取默认值
     *
     * @return -
     */
    T getDefValue();
}
