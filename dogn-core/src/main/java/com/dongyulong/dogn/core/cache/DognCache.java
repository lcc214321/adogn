package com.dongyulong.dogn.core.cache;

import com.dongyulong.dogn.core.executor.DognExecutor;
import com.dongyulong.dogn.core.monitor.DognMonitor;
import com.dongyulong.dogn.core.monitor.Monitor;
import com.dongyulong.dogn.core.monitor.indicator.IndicatorCollector;
import com.dongyulong.dogn.core.monitor.indicator.MonitorType;
import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.dongyulong.dogn.core.monitor.indicator.Indicator.CACHE_EVICTION_COUNT;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.CACHE_HIT_RATE;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.CACHE_LOAD_COUNT;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.CACHE_LOAD_FAIL;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.CACHE_LOAD_TIME_MILLIS;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.CACHE_REQ;


/**
 * 配置参数refreshAfterWrite VS expireAfterWrite:
 * <p>
 * refreshAfterWrite一般是用来进行自动刷新缓存。在refreshAfterWrite时间之内，get缓存不会自动调用reload方法（refresh时的调用）来重新填充缓存，因此get到的是旧的value。当refreshAfterWrite时间过去后，get缓存时会异步调用reload方法填充缓存。缓存填充完毕之后，get到最新的value。
 * 注意，如果不get缓存的话，是不会去自动刷新缓存的，即使refreshAfterWrite时间到达了。
 * 主动调用refresh方法会无视refreshAfterWrite，直接刷新缓存。
 * refreshAfterWrite和expireAfterWrite两个时间同时到达时会怎么样呢？如果此时进行get缓存操作，会先尝试刷新缓存。如果刷新缓存成功了，就会重置过期时间。
 *
 * @author zhangshaolong
 * @create 2021/12/15
 */
@Slf4j
public class DognCache<K, V> implements Monitor {

    public static Builder newBuilder(String bizName) {
        if (StringUtils.isEmpty(bizName)) {
            throw new IllegalArgumentException("bizName is empty!");
        }
        return new Builder(bizName);
    }

    public static class Builder {

        private String monitorName;

        private long maximumSize = 1024;

        private long expireAfterWriteNanos = TimeUnit.MINUTES.toNanos(10);

        private long refreshAfterWriteNanos = TimeUnit.MINUTES.toNanos(10);

        private ExecutorService cacheReloadExecutor;

        @SuppressWarnings("UnstableApiUsage")
        private Ticker ticker;

        private Builder(String monitorName) {
            assert StringUtils.isNotEmpty(monitorName);
            this.monitorName = monitorName;
        }

        public Builder maximumSize(long maximumSize) {
            if (maximumSize <= 0) {
                throw new IllegalArgumentException("maximumSize <= 0");
            }
            this.maximumSize = maximumSize;
            return this;
        }

        public Builder expireAfterWrite(long time, TimeUnit unit) {
            if (time <= 0) {
                throw new IllegalArgumentException("time <= 0");
            }
            if (unit == null) {
                throw new NullPointerException("unit");
            }
            expireAfterWriteNanos = unit.toNanos(time);
            return this;
        }

        public Builder refreshAfterWrite(long time, TimeUnit unit) {
            if (time <= 0) {
                throw new IllegalArgumentException("time <= 0");
            }
            if (unit == null) {
                throw new NullPointerException("unit");
            }
            refreshAfterWriteNanos = unit.toNanos(time);
            return this;
        }

        Builder ticker(@SuppressWarnings("UnstableApiUsage") Ticker ticker) {
            assert ticker != null;
            this.ticker = ticker;
            return this;
        }

        Builder withCacheReloadExecutor(ExecutorService executor) {
            assert executor != null;
            this.cacheReloadExecutor = executor;
            return this;
        }

        private ExecutorService getCacheReloadExecutor() {
            ExecutorService cacheReloadExecutor = this.cacheReloadExecutor;
            return cacheReloadExecutor != null ? cacheReloadExecutor :
                    DognExecutor.newBuilder("")
                            .corePoolSize(1).maximumPoolSize(5)
                            .keepAliveTime(60, TimeUnit.SECONDS)
                            .workQueue(new LinkedBlockingQueue<>(128))
                            .rejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy())
                            .daemon(false)
                            .build();
        }

        public <K, V> DognCache<K, V> build(final DognCacheLoader<K, V> loader) {
            if (loader == null) {
                throw new NullPointerException("loader");
            }

            CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
            builder.maximumSize(maximumSize);
            if (expireAfterWriteNanos > 0) {
                builder.expireAfterWrite(expireAfterWriteNanos, TimeUnit.NANOSECONDS);
            }
            if (refreshAfterWriteNanos > 0) {
                builder.refreshAfterWrite(refreshAfterWriteNanos, TimeUnit.NANOSECONDS);
            }
            if (ticker != null) {
                builder.ticker(ticker);
            }

            final ExecutorService cacheReloadExecutor = getCacheReloadExecutor();
            LoadingCache<K, V> cache = builder.recordStats().build(
                    new com.google.common.cache.CacheLoader<K, V>() {
                        @Override
                        public V load(@NonNull K key) throws Exception {
                            return loader._load(key);
                        }

                        @Override
                        public ListenableFuture<V> reload(final K key, V oldValue) throws Exception {
                            ListenableFutureTask<V> task = ListenableFutureTask.create(() -> loader._load(key));
                            try {
                                cacheReloadExecutor.execute(task);
                            } catch (Exception e) {
                                log.error("unable to reload key " + key, e);
                                throw e;
                            }
                            return task;
                        }
                    });

            return new DognCache<>(monitorName, cache, loader, cacheReloadExecutor);
        }
    }

    private final String monitorName;

    final LoadingCache<K, V> cache;

    private final DognCacheLoader<K, V> loader;

    private final ExecutorService cacheReloadExecutor;

    private DognCache(String monitorName, LoadingCache<K, V> cache, DognCacheLoader<K, V> loader,
                      ExecutorService cacheReloadExecutor) {
        this.monitorName = monitorName;
        this.cache = cache;
        this.loader = loader;
        this.cacheReloadExecutor = cacheReloadExecutor;
        DognMonitor.getInstance().register(this);
    }

    public V get(K key) throws ExecutionException {
        V v = cache.get(key);
        return v == loader.nullMark ? null : v;
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public void del(K key) {
        cache.invalidate(key);
    }

    public void refresh(K key) {
        cache.refresh(key);
    }

    @Override
    public MonitorType monitorType() {
        return MonitorType.CACHE;
    }

    @Override
    public void initMonitor() {

    }

    @Override
    public String monitorName() {
        return monitorName;
    }

    private volatile CacheStats preStats;

    @Override
    public void report(IndicatorCollector collector) {
        CacheStats currentStats = cache.stats();
        CacheStats stats = preStats == null ? currentStats : currentStats.minus(preStats);
        preStats = currentStats;

        collector.collect(CACHE_REQ, stats.requestCount());
        collector.collect(CACHE_HIT_RATE, stats.hitRate());
        collector.collect(CACHE_EVICTION_COUNT, stats.evictionCount());

        long loadCount = stats.loadCount();
        long loadFailCount = stats.loadExceptionCount();
        long loadTimeSum = stats.totalLoadTime() / (1000 * 1000);
        collector.collect(CACHE_LOAD_COUNT, loadCount);
        collector.collect(CACHE_LOAD_FAIL, loadFailCount);
        collector.collect(CACHE_LOAD_TIME_MILLIS, loadCount == 0 ? 0 : loadTimeSum / loadCount);
    }

    @Override
    public String toString() {
        return "AgaueCache{" +
                "monitorName='" + monitorName + '\'' +
                ", cache=" + cache +
                ", loader=" + loader +
                ", cacheReloadExecutor=" + cacheReloadExecutor +
                '}';
    }
}
