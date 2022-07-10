package com.dongyulong.dogn.core.executor;


import com.dongyulong.dogn.core.monitor.indicator.IndicatorCollector;
import com.dongyulong.dogn.core.monitor.indicator.MonitorType;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.dongyulong.dogn.core.monitor.indicator.Indicator.ACTIVE_THREAD;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.EXECUTION_COUNT;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.EXECUTION_REJECTED_COUNT;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.EXECUTION_TIME_MILLIS;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.EXECUTION_WAIT_TIME_MILLIS;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.QUEUE_SIZE;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.THREAD_SIZE;


/**
 * 自定义的线程池
 * @author zhangshaolong
 * @create 2021/12/15
 */
public class DognExecutor extends AbstractDognExecutor {

    public DognExecutor(String bizName, ExecutorService executor) {
        super(bizName, executor);
        if(StringUtils.isNotEmpty(bizName)) {
            ExecutorCollector.getInstance().addExecuteMonitor(bizName,this);
//            AgaueMonitor.getInstance().register(this);
        }
    }

    public static class Builder {
        private String bizName;
        private int corePoolSize = 10; //一般是根据cpu计算出大小值,根据是cpu密集型还是io密集性
        private int maximumPoolSize = 20;
        private long keepAliveTime = 60;
        private TimeUnit keepAliveTimeUnit = TimeUnit.SECONDS;
        private boolean daemon = false;
        private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(128);
        private RejectedExecutionHandler rejectedExecutionHandler = RejectedHandlerEnum.ABORT.getRejectHandler();

        public Builder(String bizName) {
            this.bizName = bizName;
        }

        public Builder corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public Builder maximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        public Builder keepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
            this.keepAliveTime = keepAliveTime;
            this.keepAliveTimeUnit = timeUnit;
            return this;
        }

        public Builder workQueue(BlockingQueue<Runnable> workQueue) {
            this.workQueue = workQueue;
            return this;
        }

        public Builder daemon(boolean daemon) {
            this.daemon = daemon;
            return this;
        }

        public Builder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
            return this;
        }

        public Builder rejectedExecutionHandler(RejectedHandlerEnum rejectedHandlerEnum) {
            this.rejectedExecutionHandler = rejectedHandlerEnum.getRejectHandler();
            return this;
        }

        public DognExecutor build() {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTime, keepAliveTimeUnit,
                    workQueue,
                    new DognThreadFactory(bizName, daemon),
                    rejectedExecutionHandler);

            return new DognExecutor(bizName, executor);
        }
    }

    public static Builder newBuilder(String bizName) {
        return new Builder(bizName);
    }

    @Override
    public MonitorType monitorType() {
        return MonitorType.THREAD;
    }


    /**
     * 采集线程的数据信息,需要指定
     * @param collector
     */
    @Override
    public void report(IndicatorCollector collector) {
        ExecutorMonitor monitor = monitorRef.getAndSet(new ExecutorMonitor());
        long executionCount = monitor.executionCounter.executionCount();
        collector.collect(EXECUTION_COUNT, executionCount);
        collector.collect(EXECUTION_TIME_MILLIS, executionCount == 0 ? 0 : monitor.executionCounter.timeSum() / executionCount);
        collector.collect(EXECUTION_REJECTED_COUNT, monitor.rejectedCounter.get());
        collector.collect(EXECUTION_WAIT_TIME_MILLIS, executionCount == 0 ? 0 : monitor.waitedTime.get() / executionCount);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) getExecutor();
        collector.collect(ACTIVE_THREAD, executor.getActiveCount());
        collector.collect(QUEUE_SIZE, executor.getQueue().size());
        collector.collect(THREAD_SIZE, executor.getCorePoolSize());
    }


}