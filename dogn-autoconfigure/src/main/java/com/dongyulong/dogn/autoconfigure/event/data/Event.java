package com.dongyulong.dogn.autoconfigure.event.data;

import java.io.Serializable;

/**
 * 所有时间的基础信息
 * @author zhang.shaolong
 * @create 2021/12/16
 **/
public class Event implements Serializable {

    /** 消息时间 **/
    public final long time = System.currentTimeMillis();
}
