package com.dongyulong.dogn.core.cache;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 记载数据缓存数据
 *
 * @author zhangshaolong
 * @create 2021/12/15
 **/
public abstract class DognCacheLoader<K, V> {

    public final V nullMark;

    /**
     * 监控数据
     */
    private AtomicReference<LoaderMonitor> monitorRef = new AtomicReference(new LoaderMonitor());


    protected DognCacheLoader() {
        nullMark = null;
    }

    protected DognCacheLoader(V nullMark) {
        if (nullMark == null) {
            throw new NullPointerException("nullMark");
        }
        this.nullMark = nullMark;
    }

    V _load(K key) throws Exception {
        V v = load(key);
        if (v == null) {
            LoaderMonitor monitor = monitorRef.get();

            if (hasNullMark()) {
                monitor.getLoadNullMarkCounter().incrementAndGet();
                return nullMark;
            } else {
                monitor.getLoadNullCounter().incrementAndGet();
            }
        }
        return v;
    }

    private boolean hasNullMark() {
        return nullMark != null;
    }

    public abstract V load(K key) throws Exception;
}
