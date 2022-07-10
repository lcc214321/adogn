package com.dongyulong.dogn.autoconfigure.event.publisher;


import com.dongyulong.dogn.autoconfigure.event.data.AbstractEvent;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * 发送消息事件
 *
 * @author zhang.shaolong
 * @create 2021/12/16
 **/
public interface PublisherEvent<T extends AbstractEvent> extends ApplicationEventPublisherAware {

    /**
     * 处理消息信息
     * @param t
     */
    void publisherMessage(T t);
}
