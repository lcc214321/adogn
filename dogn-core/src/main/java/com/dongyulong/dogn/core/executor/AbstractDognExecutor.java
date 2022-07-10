package com.dongyulong.dogn.core.executor;

import com.dongyulong.dogn.core.monitor.Monitor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 实现ExecutorService 基础功能
 * @author zhangshaolong
 * @create 2021/12/15
 * */
@Slf4j
public abstract class AbstractDognExecutor implements ExecutorService, Monitor {
    /**
     * 线程名
     */
    private String bizName;

    private ExecutorService executorService;

    public AbstractDognExecutor(String bizName, ExecutorService executorService) {
        this.bizName = bizName;
        this.executorService = executorService;
        addShutdownHook();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                getExecutor().shutdown();
                getExecutor().awaitTermination(5000L, TimeUnit.MILLISECONDS);
                getExecutor().shutdownNow();
            } catch (InterruptedException e) {
                log.error(" executor shutdown error: {}", bizName, e);
            } catch (Exception e) {
                log.error("executor shutdown error: {}", bizName, e);
            }
            log.info("executor shutdown: {}", bizName);
        }));
    }

    public ExecutorService getExecutor() {
        return executorService;
    }

    @Override
    public void execute(final Runnable command) {
        try {
            getExecutor().execute(new Runnable() {
                long createTime = System.currentTimeMillis();
                @Override
                public void run() {
                    monitorRef.get().waitedTime.addAndGet(System.currentTimeMillis() - createTime);
                    long startTime = System.currentTimeMillis();
                    try {
                        command.run();
                    } finally {
                        long timeCost = System.currentTimeMillis() - startTime;
                        ExecutorMonitor monitor = monitorRef.get();
                        monitor.executionCounter.recordExecution(true, timeCost);
                    }
                }
            });
        } catch (RejectedExecutionException rejectedException) {
            monitorRef.get().rejectedCounter.incrementAndGet();
            throw rejectedException;
        }
    }

    @Override
    public void shutdown() {
        getExecutor().shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return getExecutor().shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return getExecutor().isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return getExecutor().isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return getExecutor().awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        Future<T> future;
        try {
            future = getExecutor().submit(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    return task.call();
                } finally {
                    long timeCost = System.currentTimeMillis() - startTime;
                    ExecutorMonitor monitor = monitorRef.get();
                    monitor.executionCounter.recordExecution(true, timeCost);
                }
            });
        } catch (RejectedExecutionException rejectedException) {
            monitorRef.get().rejectedCounter.incrementAndGet();
            throw rejectedException;
        }

        return future;
    }

    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        RunnableFuture<T> future = new FutureTask<T>(task, result);
        execute(future);
        return future;
    }

    @Override
    public Future<?> submit(Runnable task) {
        RunnableFuture<Void> future = new FutureTask<Void>(task, null);
        execute(future);
        return future;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return getExecutor().invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return getExecutor().invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return getExecutor().invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return invokeAny(tasks, timeout, unit);
    }

    AtomicReference<ExecutorMonitor> monitorRef = new AtomicReference<>(new ExecutorMonitor());

    @Override
    public String monitorName() {
        return bizName;
    }


    /**
     * 监控
     */
    static class ExecutorMonitor {

        /**
         * 线程的一些数量信息
         */
        ExecutionCounter executionCounter = new ExecutionCounter();
        /**
         * 拒绝数量
         */
        AtomicLong rejectedCounter = new AtomicLong();

        /**
         * 等待时间
         */
        AtomicLong waitedTime = new AtomicLong();
    }

}
