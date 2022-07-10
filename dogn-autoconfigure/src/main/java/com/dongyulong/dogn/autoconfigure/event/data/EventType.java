package com.dongyulong.dogn.autoconfigure.event.data;

/**
 * 消息的定义
 * @author zhang.shaolong
 * @create 2021/12/16
 **/
public interface EventType {

    /**
     * 事件描述
     *
     * @return
     */
    String getEvent();

    /**
     * 事件code
     *
     * @return
     */
    int getEventCode();
}
