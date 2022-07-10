package com.dongyulong.dogn.core.executor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 监控打点的数据信息
 * @author zhangshaolong
 * @create 2021/12/15
 */
public class ExecutionCounter {

    /**
     * 打点的数据信息
     */
    private AtomicInteger successCounter = new AtomicInteger();
    private AtomicInteger failCounter = new AtomicInteger();
    private AtomicInteger timeoutCounter = new AtomicInteger();
    private AtomicLong timeSumCounter = new AtomicLong();

    /**
     * 统计打点数据
     * @param success
     * @param executionTime
     */
    public void recordExecution(boolean success, long executionTime) {
        if (success) {
            successCounter.incrementAndGet();
        } else {
            failCounter.incrementAndGet();
        }
        timeSumCounter.addAndGet(executionTime);
    }

    public void recordTimeout() {
        timeoutCounter.incrementAndGet();
    }

    public int successCount() {
        return successCounter.get();
    }

    public int failCount() {
        return failCounter.get();
    }

    public int timeoutCount() {
        return timeoutCounter.get();
    }

    public long timeSum() {
        return timeSumCounter.get();
    }

    public long executionCount() {
        return successCount() + failCount();
    }

}
