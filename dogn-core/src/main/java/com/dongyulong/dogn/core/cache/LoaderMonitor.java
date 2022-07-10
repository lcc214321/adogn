package com.dongyulong.dogn.core.cache;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * 监控的数据信息
 * @author zhangshaolong
 * @create 2021/12/15
 */
public class LoaderMonitor {

    /**
     * 空数据监控
     */
    private AtomicInteger loadNullCounter = new AtomicInteger();

    /**
     * 空标记信息
     */
    private AtomicInteger loadNullMarkCounter = new AtomicInteger();

    public AtomicInteger getLoadNullCounter() {
        return loadNullCounter;
    }

    public AtomicInteger getLoadNullMarkCounter() {
        return loadNullMarkCounter;
    }

}
