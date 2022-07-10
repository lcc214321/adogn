package com.dongyulong.dogn.core.method;


import com.dongyulong.dogn.core.monitor.Counter;


/**
 * 方法监控打点的数据信息，失败时间和请求次数
 * @author zhangshaolong
 * @create 2021/12/15
 */
public class MethodMonitor {

    /**
     * 打点的数据信息
     */
    private Counter successCounter = new Counter();
    private Counter failCounter = new Counter();
    private Counter methodCounter = new Counter();

    /**
     * 统计打点数据
     * @param success
     * @param executionTime
     */
    public void recordExeczution(boolean success, long executionTime) {
        if (success) {
            successCounter.inc();
        } else {
            failCounter.inc();
        }
        methodCounter.incrTime(executionTime);
    }

    public void inc() {
        methodCounter.inc();
    }

    public Counter.CounterResult getResult() {
        return methodCounter.changed();
    }

    public long getSuccResult() {
        return successCounter.changed().changed;
    }

    public long getFailResult() {
        return failCounter.changed().changed;
    }

    public long successCount() {
        return successCounter.getCount();
    }

    public long failCount() {
        return failCounter.getCount();
    }

    @Override
    public String toString() {
        return "MethodMonitor{" +
                "successCounter=" + successCounter.toString() +
                ", failCounter=" + failCounter.toString() +
                ", methodCounter=" + methodCounter.toString() +
                '}';
    }
}
