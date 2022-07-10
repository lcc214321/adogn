package com.dongyulong.dogn.autoconfigure.filter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dongy
 * @date 17:35 2022/3/2
 **/
@ConditionalOnProperty(value = "dida.monitor.http", havingValue = "false", matchIfMissing = true)
@Configuration
public class NoTracerConfiguration {

    @Bean
    public io.opentracing.Tracer jaegerTracer() {
        return io.opentracing.noop.NoopTracerFactory.create();
    }
}
