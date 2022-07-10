package com.dongyulong.dogn.autoconfigure.monitor.mybatis;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/86:38 下午
 * @since v1.0
 */
public class SqlMetrics {
    public static final double NANOSECONDS_PER_SECOND = 1E9;

    public static final double[] BUCKETS = {0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.125, 0.15, 0.175, 0.2, 0.25, 0.3, 0.4, 0.5, 0.75, 1.0, 2.5, 5.0};

    public static final Counter counter = Counter.build()
            .labelNames("method")
            .name("mapper_method_total")
            .help("mapper method cost")
            .register();

    public static final Histogram histogram = Histogram.build()
            .buckets(BUCKETS)
            .labelNames("method")
            .name("mapper_method_duration")
            .help("mapper_method_histogram")
            .register();

    public static final void recordToPrometheus(String method, long startNanos) {
        double requestCost = elapsedSecondsFromNanos(startNanos);
        String shortMethodString = method.substring(method.lastIndexOf(".", method.lastIndexOf(".") - 1) + 1);
        counter.labels(shortMethodString).inc();
        histogram.labels(shortMethodString).observe(requestCost);
    }

    public static double elapsedSecondsFromNanos(long startNanos) {
        return (System.nanoTime() - startNanos) / NANOSECONDS_PER_SECOND;
    }
}
