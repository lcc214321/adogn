package com.dongyulong.dogn.autoconfigure.filter;

import com.dongyulong.dogn.autoconfigure.filter.common.ExcludeUrl;
import com.dongyulong.dogn.autoconfigure.filter.flowcontrol.RateLimiterProperties;
import com.dongyulong.dogn.autoconfigure.filter.flowcontrol.RedisRateLimiter;
import com.dongyulong.dogn.autoconfigure.filter.interceptor.WebHandlerInterceptor;
import com.dongyulong.dogn.autoconfigure.filter.trace.TTLScopeManager;
import io.jaegertracing.internal.propagation.B3TextMapCodec;
import io.jaegertracing.micrometer.MicrometerMetricsFactory;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.prometheus.client.CollectorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.Filter;

import static org.springframework.boot.web.servlet.FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER;

/**
 * @author zhangshaolong
 * @create 2021/12/17
 **/
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 100)
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "dida.monitor", name = "http", havingValue = "true")
public class WebConfiguration extends WebMvcConfigurerAdapter {

    @Value("${spring.application.name}")
    private String appName;

    /**
     * 返回格式的请求
     *
     * @return
     */
    @Bean
    @ConditionalOnClass({ResponseHeaderFilter.class})
    public Filter responseHeaderFilter() {
        return new ResponseHeaderFilter();
    }

    /**
     * dd的客户端信息拦截信息
     *
     * @return
     */
    @Bean
    @ConditionalOnClass({DdcinfoFilter.class})
    @Order(REQUEST_WRAPPER_FILTER_MAX_ORDER - 49)
    public Filter ddcinfoFilter() {
        return new DdcinfoFilter();
    }

    /**
     * 监控的返回打点
     *
     * @return
     */
    @Bean
    @ConditionalOnClass({MonitorFilter.class})
    @Order(REQUEST_WRAPPER_FILTER_MAX_ORDER - 50)
    public MonitorFilter monitorFilter() {
        return new MonitorFilter();
    }


    @Bean
    @ConfigurationProperties(prefix = "flowcontrol.hotparams")
    public RateLimiterProperties rateLimiterProperties() {
        return new RateLimiterProperties();
    }

    @Bean
    public RedisRateLimiter rateLimiter(RateLimiterProperties properties) {
        return new RedisRateLimiter(properties);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //web的的拦截器
        registry.addInterceptor(new WebHandlerInterceptor())
                .excludePathPatterns(String.valueOf(ExcludeUrl.EXCLUDE_PATHS_LIST));
    }

    /**
     * 修复 micrometer 不打印原生 prometheus metrics
     *
     * @return
     */
    @Bean
    public CollectorRegistry collectorRegistry() {
        return CollectorRegistry.defaultRegistry;
    }

    @Bean("tracer")
    public Tracer tracer(PrometheusMeterRegistry registry) {
        B3TextMapCodec injector = new B3TextMapCodec();
        Metrics.addRegistry(registry);
        MicrometerMetricsFactory metricsFactory = new MicrometerMetricsFactory();
        io.jaegertracing.Configuration configuration = new io.jaegertracing.Configuration(appName);
        return configuration.getTracerBuilder()
                .withMetricsFactory(metricsFactory)
                .withScopeManager(new TTLScopeManager())
                .registerInjector(Format.Builtin.HTTP_HEADERS, injector)
                .registerExtractor(Format.Builtin.HTTP_HEADERS, injector)
                .build();
    }
}
