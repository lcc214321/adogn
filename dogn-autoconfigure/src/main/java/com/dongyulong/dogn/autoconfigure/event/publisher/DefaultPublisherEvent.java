package com.dongyulong.dogn.autoconfigure.event.publisher;


import com.dongyulong.dogn.autoconfigure.event.data.AbstractEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

/**
 * 默认发送消息
 * @author zhang.shaolong
 * @create 2021/12/16
 **/
public class DefaultPublisherEvent<T extends AbstractEvent> implements PublisherEvent<T> {

    private ApplicationEventPublisher publisher;

    @Override
    public void publisherMessage(T t) {
        Optional.ofNullable(t).ifPresent(b -> publisher.publishEvent(t));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
