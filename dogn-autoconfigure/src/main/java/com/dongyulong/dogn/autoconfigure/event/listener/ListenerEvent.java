package com.dongyulong.dogn.autoconfigure.event.listener;

import com.dongyulong.dogn.autoconfigure.event.data.AbstractEvent;
import org.springframework.context.ApplicationListener;

/**
 * 所有的监听着继承这个类
 *
 * @author zhang.shaolong
 * @create 2021/12/16
 **/
public interface ListenerEvent<T extends AbstractEvent> extends ApplicationListener<T> {
}
