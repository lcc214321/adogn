package com.dongyulong.dogn.core.tx;

import org.springframework.core.NamedThreadLocal;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于事务扩展的帮助类
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/97:09 上午
 * @since v1.0
 */
public class TransactionExtHelper {
    private static final ThreadLocal<Map<String, DognSynchronization>> syncronizations = new NamedThreadLocal<Map<String, DognSynchronization>>("Transactional-DIDA") {
        @Override
        protected Map<String, DognSynchronization> initialValue() {
            return new ConcurrentHashMap<String, DognSynchronization>();
        }
    };
    private static final String NULL_VALUE = "$$NULL$$";

    private static DognSynchronization getMcSynchronization() {
        Map<String, DognSynchronization> didaSynchronizations = syncronizations.get();
        String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        DognSynchronization synchronization = didaSynchronizations.get(currentTransactionName);
        if (null == synchronization) {
            synchronized (TransactionExtHelper.class) {
                synchronization = didaSynchronizations.get(currentTransactionName);
                if (null == synchronization) {
                    synchronization = new DognSynchronization(currentTransactionName);
                    didaSynchronizations.put(currentTransactionName, synchronization);
                    TransactionSynchronizationManager.registerSynchronization(synchronization);
                }
            }
        }
        return synchronization;
    }

    public static void addCommittedCallback(ITxCommittedCallback callback) {
        DognSynchronization synchronization = getMcSynchronization();
        synchronization.addCallback(callback);
    }

    public static void addRolledbackCallback(ITxRolledbackCallback callback) {
        DognSynchronization synchronization = getMcSynchronization();
        synchronization.addCallback(callback);
    }

    public static void addCompltedCallback(ITxCompletedCallback callback) {
        DognSynchronization synchronization = getMcSynchronization();
        synchronization.addCallback(callback);
    }

    /**
     * 判断当前线程是否正在事务中
     *
     * @return
     */
    public static boolean isTransactionSynchronizationActive() {
        return TransactionSynchronizationManager.isSynchronizationActive();
    }

    public static Map<String, Object> getLocalTxCache() {
        DognSynchronization synchronization = getMcSynchronization();
        return synchronization.getLocalTxCache();
    }

    public static void resetSynchronization() {
        Map<String, DognSynchronization> didaSynchronizations = syncronizations.get();
        String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        DognSynchronization synchronization = didaSynchronizations.remove(currentTransactionName);
        if (null != synchronization) {
            synchronization.reset();
        }
        if (didaSynchronizations.isEmpty()) {
            syncronizations.remove();
        }
    }

    private static Object getTxValue(Object value) {
        if (null == value) {
            return NULL_VALUE;
        }
        return value;
    }

    /**
     * 添加一个缓存到当前事务的本地缓存中，在事务提交时执行runnable
     *
     * @param key
     * @param value
     * @param getCallable 如果本地缓存不存在，则通过getCallable中获取。 force为true时可以不提供getCallable
     * @param runnable
     * @param force       true：存在更新，不存在新增；false:仅相应key不存在时添加
     */
    public static <T> void addLocalTxCache(String key, Object value, final Callable<T> getCallable, final Runnable runnable, boolean force) {
        if (isTransactionSynchronizationActive()) {
            Map<String, Object> localCache = getLocalTxCache();
            if (force) {
                localCache.put(key, getTxValue(value));
            } else if (!localCache.containsKey(key)) {
                try {
                    T currentValue = getCallable.call();
                    if (null == currentValue) {
                        //force=false，只有不存在才新建
                        localCache.put(key, getTxValue(value));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("call getCallable faild.", e);
                }
            } else if (NULL_VALUE.equals(localCache.get(key))) {
                localCache.put(key, getTxValue(value));
            }

            TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                @Override
                public void afterCommitted() {
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }

    /**
     * 从当前事务的本地缓存中删除一个键，在事务提交时执行runnable
     *
     * @param key
     * @param runnable
     */
    public static void deleteLocalTxCache(String key, final Runnable runnable) {
        if (isTransactionSynchronizationActive()) {
            Map<String, Object> localCache = getLocalTxCache();
            localCache.put(key, NULL_VALUE);

            TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                @Override
                public void afterCommitted() {
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }

    /**
     * 从当前事务的本地缓存中删除一组key，在事务提交时执行runnable
     *
     * @param keys
     * @param runnable
     */
    public static void deleteLocalTxCache(String[] keys, final Runnable runnable) {
        if (isTransactionSynchronizationActive()) {
            Map<String, Object> localCache = getLocalTxCache();
            for (String key : keys) {
                localCache.put(key, NULL_VALUE);
            }

            TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                @Override
                public void afterCommitted() {
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }

    /**
     * 更新当前事务的缓存中，在事务提交时执行runnable
     *
     * @param key
     * @param value
     * @param getCallable 如果本地缓存不存在，则通过getCallable中获取。 force为true时可以不提供getCallable
     * @param runnable
     * @param force       true:存在更新，不存在新增；false:不存在忽略
     */
    public static <T> void updateLocalTxCache(String key, Object value, final Callable<T> getCallable, final Runnable runnable, boolean force) {
        if (isTransactionSynchronizationActive()) {
            Map<String, Object> localCache = getLocalTxCache();
            if (force) {
                localCache.put(key, getTxValue(value));
            } else if (!localCache.containsKey(key)) {
                try {
                    T currentValue = getCallable.call();
                    if (null != currentValue) {
                        //force=false，只有存在才更新
                        localCache.put(key, getTxValue(value));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("call getCallable faild.", e);
                }
            } else if (!NULL_VALUE.equals(localCache.get(key))) {
                localCache.put(key, getTxValue(value));
            }

            TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                @Override
                public void afterCommitted() {
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }

    /**
     * 获取缓存，优先从本地缓存中获取，如果没有则从callable中获取
     *
     * @param key
     * @param type
     * @param callable
     * @return
     */
    public static <T> T selectLocalTxCache(String key, Class<T> type, final Callable<T> callable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> localCache = getLocalTxCache();
                if (localCache.containsKey(key)) {
                    Object currentValue = localCache.get(key);
                    if (NULL_VALUE.equals(currentValue)) {
                        return null;
                    }
                    return cast(currentValue, type);
                } else {
                    return callable.call();
                }
            } else {
                return callable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("get cache faild.", e);
        }
    }

    /**
     * 获取缓存，优先从本地缓存中获取，如果没有则从callable中获取
     *
     * @param keys
     * @param callable
     * @return
     */
    public static Map<String, Object> selectLocalTxCache(String[] keys, final Callable<Map<String, Object>> callable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> localCache = getLocalTxCache();
                Map<String, Object> result = new HashMap<String, Object>();
                result.putAll(callable.call());
                for (String key : keys) {
                    if (localCache.containsKey(key)) {
                        Object currentValue = localCache.get(key);
                        if (NULL_VALUE.equals(currentValue)) {
                            result.remove(key);
                        } else {
                            result.put(key, cast(currentValue, Object.class));
                        }
                    }
                }
                return result;
            } else {
                return callable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("get cache faild.", e);
        }
    }

    /**
     * 获取缓存的所有键，包含本地缓存和callable中所有键
     *
     * @param callable
     * @return
     */
    public static String[] keysFromLocalTxCache(final Callable<String[]> callable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> localCache = getLocalTxCache();
                Set<String> currentKeys = new TreeSet<String>();
                currentKeys.addAll(Arrays.asList(callable.call()));
                Iterator<Map.Entry<String, Object>> iterator = localCache.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    String entryKey = entry.getKey();
                    Object entryValue = entry.getValue();
                    if (null == entryValue || NULL_VALUE.equals(entryValue)) {
                        currentKeys.remove(entryKey);
                    } else {
                        currentKeys.add(entryKey);
                    }
                }
                return currentKeys.toArray(new String[currentKeys.size()]);
            } else {
                return callable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("get keys faild.", e);
        }
    }

    /**
     * 整数自增，在事务提交时执行incrCallable，本地缓存中没有取到通过getCallable获取初始值
     * 注意：如果当前key对应值不存在则使用默认值，而不是默认值自增后的值
     *
     * @param key
     * @param defaultValue
     * @param incrValue
     * @param getCallable
     * @param incrCallable
     * @return
     */
    public static Long incr(String key, long defaultValue, long incrValue, final Callable<Long> getCallable, final Callable<Long> incrCallable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> localCache = getLocalTxCache();
                long newValue = defaultValue;
                if (localCache.containsKey(key)) {
                    Object currentValue = localCache.get(key);
                    if (null != currentValue && !NULL_VALUE.equals(currentValue)) {
                        newValue = cast(currentValue, Long.class).longValue() + incrValue;
                    }
                } else {
                    Object currentValue = getCallable.call();
                    if (null != currentValue) {
                        newValue = cast(currentValue, Long.class).longValue() + incrValue;
                    }
                }
                localCache.put(key, newValue);

                TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                    @Override
                    public void afterCommitted() {
                        try {
                            incrCallable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return newValue;
            } else {
                return incrCallable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("cache incr faild.", e);
        }
    }

    /**
     * 整数自减，在事务提交时执行decrCallable，本地缓存中没有取到通过getCallable获取初始值
     * 注意：如果当前key对应值不存在则使用默认值，而不是默认值自减后的值
     *
     * @param key
     * @param defaultValue
     * @param decrValue
     * @param getCallable
     * @param decrCallable
     * @return
     */
    public static Long decr(String key, long defaultValue, long decrValue, final Callable<Long> getCallable, final Callable<Long> decrCallable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> localCache = getLocalTxCache();
                long newValue = defaultValue;
                if (localCache.containsKey(key)) {
                    Object currentValue = localCache.get(key);
                    if (null != currentValue && !NULL_VALUE.equals(currentValue)) {
                        newValue = cast(currentValue, Long.class).longValue() + decrValue;
                    }
                } else {
                    Object currentValue = getCallable.call();
                    if (null != currentValue) {
                        newValue = cast(currentValue, Long.class).longValue() + decrValue;
                    }
                }
                localCache.put(key, newValue);

                TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                    @Override
                    public void afterCommitted() {
                        try {
                            decrCallable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return newValue;
            } else {
                return decrCallable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("cache decr faild.", e);
        }
    }

    /**
     * 整数自增，在事务提交时执行incrCallable，本地缓存中没有取到通过getCallable获取初始值
     * 注意：如果当前key对应值不存在则使用默认值，而不是默认值自增后的值
     *
     * @param key
     * @param defaultValue
     * @param incrValue
     * @param getCallable
     * @param incrCallable
     * @return
     */
    public static Double incr(String key, double defaultValue, double incrValue, final Callable<Double> getCallable, final Callable<Double> incrCallable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> localCache = getLocalTxCache();
                double newValue = defaultValue;
                if (localCache.containsKey(key)) {
                    Object currentValue = localCache.get(key);
                    if (null != currentValue && !NULL_VALUE.equals(currentValue)) {
                        newValue = cast(currentValue, Double.class).doubleValue() + incrValue;
                    }
                } else {
                    Object currentValue = getCallable.call();
                    if (null != currentValue) {
                        newValue = cast(currentValue, Double.class).doubleValue() + incrValue;
                    }
                }
                localCache.put(key, newValue);

                TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                    @Override
                    public void afterCommitted() {
                        try {
                            incrCallable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return newValue;
            } else {
                return incrCallable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("cache incr faild.", e);
        }
    }

    /**
     * 整数自减，在事务提交时执行decrCallable，本地缓存中没有取到通过getCallable获取初始值
     * 注意：如果当前key对应值不存在则使用默认值，而不是默认值自减后的值
     *
     * @param key
     * @param defaultValue
     * @param decrValue
     * @param getCallable
     * @param decrCallable
     * @return
     */
    public static Double decr(String key, double defaultValue, double decrValue, final Callable<Double> getCallable, final Callable<Double> decrCallable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> localCache = getLocalTxCache();
                double newValue = defaultValue;
                if (localCache.containsKey(key)) {
                    Object currentValue = localCache.get(key);
                    if (null != currentValue && !NULL_VALUE.equals(currentValue)) {
                        newValue = cast(currentValue, Double.class).doubleValue() - decrValue;
                    }
                } else {
                    Object currentValue = getCallable.call();
                    if (null != currentValue) {
                        newValue = cast(currentValue, Double.class).doubleValue() - decrValue;
                    }
                }
                localCache.put(key, newValue);

                TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                    @Override
                    public void afterCommitted() {
                        try {
                            decrCallable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return newValue;
            } else {
                return decrCallable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("cache decr faild.", e);
        }
    }

    private static Map<String, Object> getTxHash(String key) {
        Map<String, Object> localCache = getLocalTxCache();
        Object currentValue = localCache.get(key);
        Map<String, Object> hash = null;
        if (null == currentValue || NULL_VALUE.equals(currentValue)) {
            hash = new HashMap<String, Object>();
            localCache.put(key, hash);
        } else {
            hash = (Map<String, Object>) currentValue;
        }
        return hash;
    }

    public static <T> void haddLocalTxCache(String key, String field, Object value, final Callable<T> getCallable, final Runnable runnable, boolean force) {
        if (isTransactionSynchronizationActive()) {
            Map<String, Object> hash = getTxHash(key);
            if (force) {
                hash.put(field, getTxValue(value));
            } else if (!hash.containsKey(field)) {
                try {
                    T currentValue = getCallable.call();
                    if (null == currentValue) {
                        //force=false，只有不存在才新建
                        hash.put(field, getTxValue(value));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("call getCallable faild.", e);
                }
            } else if (NULL_VALUE.equals(hash.get(field))) {
                hash.put(field, getTxValue(value));
            }

            TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                @Override
                public void afterCommitted() {
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }

    public static void hdeleteLocalTxCache(String key, String field, final Runnable runnable) {
        if (isTransactionSynchronizationActive()) {
            Map<String, Object> hash = getTxHash(key);
            hash.put(field, NULL_VALUE);

            TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                @Override
                public void afterCommitted() {
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }

    public static void hdeleteLocalTxCache(String key, String[] fields, final Runnable runnable) {
        if (isTransactionSynchronizationActive()) {
            Map<String, Object> hash = getTxHash(key);
            for (String field : fields) {
                hash.put(field, NULL_VALUE);
            }

            TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                @Override
                public void afterCommitted() {
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }

    public static <T> void hupdateLocalTxCache(String key, String field, Object value, final Callable<T> getCallable, final Runnable runnable, boolean force) {
        if (isTransactionSynchronizationActive()) {
            Map<String, Object> hash = getTxHash(key);
            if (force) {
                hash.put(field, getTxValue(value));
            } else if (!hash.containsKey(field)) {
                try {
                    T currentValue = getCallable.call();
                    if (null != currentValue) {
                        //force=false，只有存在才更新
                        hash.put(field, getTxValue(value));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("call getCallable faild.", e);
                }
            } else if (!NULL_VALUE.equals(hash.get(field))) {
                hash.put(field, getTxValue(value));
            }

            TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                @Override
                public void afterCommitted() {
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }

    public static <T> T hSelectLocalTxCache(String key, String field, Class<T> fieldType, final Callable<T> callable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> hash = getTxHash(key);
                if (hash.containsKey(field)) {
                    Object fieldValue = hash.get(field);
                    if (NULL_VALUE.equals(fieldValue)) {
                        return null;
                    }
                    return cast(fieldValue, fieldType);
                } else {
                    return callable.call();
                }
            } else {
                return callable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("get hash field faild.", e);
        }
    }

    public static Map<String, Object> hselectLocalTxCache(String key, String[] fields, final Callable<Map<String, Object>> callable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> hash = getTxHash(key);
                Map<String, Object> result = new HashMap<String, Object>();
                result.putAll(callable.call());
                for (String field : fields) {
                    if (hash.containsKey(key)) {
                        Object currentValue = hash.get(key);
                        if (NULL_VALUE.equals(currentValue)) {
                            result.remove(key);
                        } else {
                            result.put(key, cast(currentValue, Object.class));
                        }
                    }
                }
                return result;
            } else {
                return callable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("get cache faild.", e);
        }
    }

    public static String[] hKeysFromLocalTxCache(String key, final Callable<String[]> callable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> hash = getTxHash(key);
                Set<String> currentKeys = new TreeSet<String>();
                currentKeys.addAll(Arrays.asList(callable.call()));
                Iterator<Map.Entry<String, Object>> iterator = hash.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    String entryKey = entry.getKey();
                    Object entryValue = entry.getValue();
                    if (null == entryValue || NULL_VALUE.equals(entryValue)) {
                        currentKeys.remove(entryKey);
                    } else {
                        currentKeys.add(entryKey);
                    }
                }
                return currentKeys.toArray(new String[currentKeys.size()]);
            } else {
                return callable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("get hash fields faild.", e);
        }
    }

    public static Long hincr(String key, String field, long defaultValue, long incrValue, final Callable<Long> getCallable, final Callable<Long> incrCallable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> hash = getTxHash(key);
                long newValue = defaultValue;
                if (hash.containsKey(field)) {
                    Object currentValue = hash.get(field);
                    if (null != currentValue && !NULL_VALUE.equals(currentValue)) {
                        newValue = cast(currentValue, Long.class).longValue() + incrValue;
                    }
                } else {
                    Object currentValue = getCallable.call();
                    if (null != currentValue) {
                        newValue = cast(currentValue, Long.class).longValue() + incrValue;
                    }
                }
                TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                    @Override
                    public void afterCommitted() {
                        try {
                            incrCallable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                hash.put(field, newValue);
                return newValue;
            } else {
                return incrCallable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("hash field incr faild.", e);
        }
    }

    public static Long hdecr(String key, String field, long defaultValue, long decrValue, final Callable<Long> getCallable, final Callable<Long> decrCallable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> hash = getTxHash(key);
                long newValue = defaultValue;
                if (hash.containsKey(field)) {
                    Object currentValue = hash.get(field);
                    if (null != currentValue && !NULL_VALUE.equals(currentValue)) {
                        newValue = cast(currentValue, Long.class).longValue() - decrValue;
                    }
                } else {
                    Object currentValue = getCallable.call();
                    if (null != currentValue) {
                        newValue = cast(currentValue, Long.class).longValue() - decrValue;
                    }
                }
                TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                    @Override
                    public void afterCommitted() {
                        try {
                            decrCallable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                hash.put(field, newValue);
                return newValue;
            } else {
                return decrCallable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("hash field decr faild.", e);
        }
    }

    public static Double hincr(String key, String field, double defaultValue, double incrValue, final Callable<Double> getCallable, final Callable<Double> decrCallable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> hash = getTxHash(key);
                double newValue = defaultValue;
                if (hash.containsKey(field)) {
                    Object currentValue = hash.get(field);
                    if (null != currentValue && !NULL_VALUE.equals(currentValue)) {
                        newValue = cast(currentValue, Double.class).doubleValue() + incrValue;
                    }
                } else {
                    Object currentValue = getCallable.call();
                    if (null != currentValue) {
                        newValue = cast(currentValue, Double.class).doubleValue() + incrValue;
                    }
                }
                TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                    @Override
                    public void afterCommitted() {
                        try {
                            decrCallable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                hash.put(field, newValue);
                return newValue;
            } else {
                return decrCallable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("hash field incr faild.", e);
        }
    }

    public static Double hdecr(String key, String field, double defaultValue, double decrValue, final Callable<Double> getCallable, final Callable<Double> decrCallable) {
        try {
            if (isTransactionSynchronizationActive()) {
                Map<String, Object> hash = getTxHash(key);
                double newValue = defaultValue;
                if (hash.containsKey(field)) {
                    Object currentValue = hash.get(field);
                    if (null != currentValue && !NULL_VALUE.equals(currentValue)) {
                        newValue = cast(currentValue, Double.class).doubleValue() - decrValue;
                    }
                } else {
                    Object currentValue = getCallable.call();
                    if (null != currentValue) {
                        newValue = cast(currentValue, Double.class).doubleValue() - decrValue;
                    }
                }
                TransactionExtHelper.addCommittedCallback(new ITxCommittedCallback() {
                    @Override
                    public void afterCommitted() {
                        try {
                            decrCallable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                hash.put(field, newValue);
                return newValue;
            } else {
                return decrCallable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException("hash field decr faild.", e);
        }
    }

    private static <T> T cast(Object value, Class<T> type) {
        if (null == value) {
            return type.cast(value);
        }

        if (type.isInstance(value)) {
            return type.cast(value);
        } else if (type == String.class) {
            return type.cast(value.toString());
        } else if (type == Byte.class) {
            return type.cast(Byte.parseByte(value.toString()));
        } else if (type == Short.class) {
            return type.cast(Short.parseShort(value.toString()));
        } else if (type == Integer.class) {
            return type.cast(Integer.parseInt(value.toString()));
        } else if (type == Long.class) {
            return type.cast(Long.parseLong(value.toString()));
        } else if (type == Float.class) {
            return type.cast(Float.parseFloat(value.toString()));
        } else if (type == Double.class) {
            return type.cast(Double.parseDouble(value.toString()));
        } else if (type == Boolean.class) {
            return type.cast(Boolean.parseBoolean(value.toString()));
        } else if (type == Character.class) {
            return type.cast(value.toString().charAt(0));
        } else if (type == BigDecimal.class) {
            return type.cast(new BigDecimal(value.toString()));
        } else if (type == BigInteger.class) {
            return type.cast(new BigInteger(value.toString()));
        } else if (type == Date.class && value.toString().matches("\\d+\\.?\\d*")) {
            return type.cast(new Date(Long.parseLong(value.toString())));
        }
        return type.cast(value);
    }
}
