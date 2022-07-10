package com.dongyulong.dogn.core.tx;

/**
 * 事务提交成功后的回调接口
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/97:08 上午
 * @since v1.0
 */
public interface ITxCommittedCallback extends ITxCallback {
    /**
     * 提交成功后
     */
    void afterCommitted();
}

