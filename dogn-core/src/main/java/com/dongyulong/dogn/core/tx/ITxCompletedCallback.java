package com.dongyulong.dogn.core.tx;

/**
 * 事务完成时（不论成功或失败）
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/97:08 上午
 * @since v1.0
 */
public interface ITxCompletedCallback extends ITxCallback {

    void afterCompletion();
}
