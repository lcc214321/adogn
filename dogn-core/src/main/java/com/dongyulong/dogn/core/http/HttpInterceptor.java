package com.dongyulong.dogn.core.http;

import com.dongyulong.dogn.core.log.LoggerBuilder;
import com.dongyulong.dogn.core.monitor.Counter;
import com.dongyulong.dogn.core.monitor.Monitor;
import com.dongyulong.dogn.core.monitor.indicator.IndicatorCollector;
import com.dongyulong.dogn.core.monitor.indicator.MonitorType;
import io.prometheus.client.CollectorRegistry;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.dongyulong.dogn.core.monitor.indicator.Indicator.HTTP_CODE_4;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.HTTP_CODE_5;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.HTTP_ERROR;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.HTTP_QPS;
import static com.dongyulong.dogn.core.monitor.indicator.Indicator.HTTP_TIME;


/**
 * 日志打点统计信息
 * @author zhangshaolong
 * @create 2022/1/26
 * */
@Slf4j
public class HttpInterceptor implements Interceptor, Monitor {

    private static final Logger LOGGER = LoggerBuilder.getLogger("http");

    private final static HttpCollector httpCollector = new HttpCollector().register(CollectorRegistry.defaultRegistry);

    private String httpName;

    private HttpMonitor httpMonitor;

    public HttpInterceptor(String httpName) {
        this.httpName = httpName;
        httpMonitor = new HttpMonitor();
        httpCollector.addHttpMonitor(httpName,httpMonitor);
//        AgaueMonitor.getInstance().register(this);
    }

    /**
     * 日志信息 name:url:get:code:result:
     * 拦截请求的日志信息,并对数据进行统计打点
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String logMessag = httpName + ":" + request.url() + ":" + request.method();
        long startNs = System.nanoTime();
        long tookMs;
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log.error(logMessag, e);
            throw new IOException();
        } finally {
            tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            int code = response == null ? -1 : response.code();
            logMessag += ":" + code + ":" + (response == null || response.message().isEmpty() ? "" : response.message()) + ":" + tookMs + "ms";
            httpMonitor.record(response != null,code,tookMs);
            LOGGER.info(logMessag);
        }
        return response;
    }

    @Override
    public MonitorType monitorType() {
        return MonitorType.HTTP;
    }

    @Override
    public String monitorName() {
        return httpName;
    }

    /**
     * 上报采集数据
     * @param collector
     */
    @Override
    public void report(IndicatorCollector collector) {
        Counter.CounterResult counterResult = httpMonitor.qpsCounter.changed();
        collector.collect(HTTP_QPS, counterResult.changed);
        collector.collect(HTTP_CODE_4, httpMonitor.code4xx.changed().changed);
        collector.collect(HTTP_CODE_5, httpMonitor.code5xx.changed().changed);
        collector.collect(HTTP_ERROR, httpMonitor.failCounter.changed().changed);
        collector.collect(HTTP_TIME, counterResult.avgCostMilSeconds);
    }
}
