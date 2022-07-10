package com.dongyulong.dogn.core.tx;

/**
 * 事务回滚后的回调接口
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/97:09 上午
 * @since v1.0
 */
public interface ITxRolledbackCallback extends ITxCallback {
    /**
     * 事务回滚后
     */
    void afterRolledback();
}
