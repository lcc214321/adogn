package com.dongyulong.dogn.core.monitor;

import java.util.concurrent.atomic.LongAdder;

/**
 * 统计计数信息
 *
 * @author zhangshaolong
 * @create 2021/11/22
 **/
public class Counter {

    private LongAdder count = new LongAdder();
    private LongAdder time = new LongAdder();

    private volatile long prevCount =0L;
    private volatile long preTime = 0L;

    public void inc(int count) {
        this.count.add(count);
    }

    public void inc() {
         this.count.increment();
    }

    public void dec(int count) {
        this.count.add(-count);
    }

    public void dec() {
        this.count.decrement();
    }

    public void incrTime(long milseconds){
        this.time.add(milseconds);
    }

    public void clear() {
        prevCount = 0;
    }

    public long getCount() {
        return count.longValue();
    }

    public CounterResult changed(){
        long now = getCount();
        long change = now - this.prevCount;
        this.prevCount = now;

        long times = this.time.longValue();
        long tchange = times - this.preTime;
        this.preTime = times;

        //计算平均值
        CounterResult result = new CounterResult();
        result.changed = change;
        result.times = tchange;
        if (change > 0) {
            result.avgCostMilSeconds = (tchange / change);
        }
        return result;
    }

    public static class CounterResult{
        public long changed;
        public long times;
        public long avgCostMilSeconds;
    }

    @Override
    public String toString() {
        return "Counter{" +
                "count=" + count +
                ", time=" + time +
                ", prevCount=" + prevCount +
                ", preTime=" + preTime +
                '}';
    }
}
