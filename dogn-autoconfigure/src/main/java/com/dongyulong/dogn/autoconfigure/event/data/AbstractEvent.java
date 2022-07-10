package com.dongyulong.dogn.autoconfigure.event.data;

import org.springframework.context.ApplicationEvent;

/**
 * 所有事件的基础类
 *
 * @author zhang.shaolong
 * @create 2021/12/16
 **/
public abstract class AbstractEvent<T extends Event> extends ApplicationEvent {

    /**
     * 消息类型
     */
    private EventType eventType;

    public AbstractEvent(Object source, EventType eventType) {
        super(source);
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public T getData() {
        return (T)this.getSource();
    }
}
