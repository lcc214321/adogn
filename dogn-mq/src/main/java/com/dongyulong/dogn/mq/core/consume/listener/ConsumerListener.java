package com.dongyulong.dogn.mq.core.consume.listener;

import org.apache.rocketmq.common.message.MessageExt;

/**
 * @author zhangshaolong
 * @create 2022/1/18
 **/
public interface ConsumerListener {

    /**
     * 处理失败的数据信息
     */
    boolean onErrorRetry(MessageExt messageExt,Throwable e);

    /**
     * 处理成功的消息
     */
    void onSuccess(MessageExt messageExt);
}
