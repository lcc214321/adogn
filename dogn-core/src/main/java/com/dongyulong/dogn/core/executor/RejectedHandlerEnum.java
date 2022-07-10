package com.dongyulong.dogn.core.executor;

import lombok.AllArgsConstructor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 拒绝策略枚举
 * @author zhangshaolong
 * @create 2021/12/15
 */
@AllArgsConstructor
public enum RejectedHandlerEnum {

    /**
     *抛出 RejectedExecutionException
     */
    ABORT(new ThreadPoolExecutor.AbortPolicy()),

    /**
     * 会在线程池当前正在运行任务
     */
    CALLER_RUNS(new ThreadPoolExecutor.CallerRunsPolicy()),

    /**
     * 丢弃最早的未处理的任务请求
     */
    DISCARD_OLDEST(new ThreadPoolExecutor.DiscardOldestPolicy()),

    /**
     * 当任务添加到线程池中被拒绝时，线程池将丢弃被拒绝的任务。也即不处理新任务，直接丢弃掉。
     */
    DISCARD(new ThreadPoolExecutor.DiscardPolicy());

    private final RejectedExecutionHandler rejectedExecutionHandler;

    public RejectedExecutionHandler getRejectHandler() {
        return rejectedExecutionHandler;
    }
}
