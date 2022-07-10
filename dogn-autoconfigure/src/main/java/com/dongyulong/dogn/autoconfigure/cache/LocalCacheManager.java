package com.dongyulong.dogn.autoconfigure.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 本地缓存管理
 *
 * @author zhang.shaolong
 * @create 2021/12/16
 **/
public class LocalCacheManager implements CacheManager {

    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();

    private Set<String> cacheNames = new LinkedHashSet<String>();

    /**
     * 添加cach缓存信息
     *
     * @param cache
     */
    public void addCache(Cache cache) {
        if (cache != null) {
            this.cacheMap.putIfAbsent(cache.getName(), cache);
            this.cacheNames.add(cache.getName());
        }
    }

    @Override
    public Cache getCache(String name) {
        return this.cacheMap.get(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheNames);
    }

}
