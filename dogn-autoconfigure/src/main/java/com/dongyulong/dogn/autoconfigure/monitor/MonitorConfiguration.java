package com.dongyulong.dogn.autoconfigure.monitor;

import com.dongyulong.dogn.autoconfigure.monitor.handle.HttpAopCollector;
import com.dongyulong.dogn.autoconfigure.monitor.handle.MethodAopCollector;
import com.dongyulong.dogn.autoconfigure.monitor.handle.ThriftAopCollector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 异常的处理拦截
 * @author zhangshaolong
 * @create 2021/11/22
 **/
@Configuration
public class MonitorConfiguration {

    private final static String MONITOR_PREFIX = "dida.monitor";

    /**
     * 扫描拦截
     * @return
     */
    @Bean
    @ConditionalOnClass({MonitorPostProcessor.class})
    public MonitorPostProcessor monitorPostProcessor() {
        return new MonitorPostProcessor();
    }

    /**
     * 方法的拦截,主要是打印方法的一些时间统计信息,自定义方法的一些打点
     * @return
     */
    @Bean
    @ConditionalOnClass({MethodAopCollector.class})
    public MethodMonitor methodHandle() {
        return new MethodMonitor(new MethodAopCollector());
    }


    /**
     * http请求的统一拦截
     * @return
     */
    @Bean
    @ConditionalOnClass({HttpAopCollector.class})
    @ConditionalOnProperty(prefix = MONITOR_PREFIX, name = "http", havingValue = "true")
    public RsMonitor rsMonitor() {
        return new RsMonitor(new HttpAopCollector());
    }


    /**
     * thrift请求的统一拦截
     * @return
     */
    @Bean
    @ConditionalOnClass({ThriftAopCollector.class})
    @ConditionalOnProperty(prefix = MONITOR_PREFIX, name = "thrift", havingValue = "true")
    public ThriftMonitor thriftMonitor() {
        return new ThriftMonitor(new ThriftAopCollector());
    }
}
