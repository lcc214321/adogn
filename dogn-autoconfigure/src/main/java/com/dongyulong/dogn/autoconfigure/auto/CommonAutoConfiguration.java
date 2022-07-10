package com.dongyulong.dogn.autoconfigure.auto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.ServerSocket;

/**
 * @author zhangshaolong
 * @create 2022/1/21
 **/
@Configuration
@Slf4j
public class CommonAutoConfiguration {

    private static final int PORT_RANGE_START = 10000;
    private static final int PORT_RANGE_END = 20000;

    @Bean
    @ConditionalOnClass(name = "com.didapinche.server.commons.common.util.LoadPropertyUtil")
    Object loadPropertyUtil() throws Exception {
        Class clz = Class.forName("com.didapinche.server.commons.common.util.LoadPropertyUtil");
        return clz.newInstance();
    }

    @Bean
    @ConditionalOnClass(name = "com.didapinche.server.commons.common.spring.SpringUtils")
    public Object springUtils() throws Exception {
        Class clz = Class.forName("com.didapinche.server.commons.common.spring.SpringUtils");
        return clz.newInstance();
    }

    /**
     * 加载thrift的服务启动
     * @return
     */
    @Bean
    @ConditionalOnProperty("thrift.server.interface")
    public EmbeddedServletContainerCustomizer embeddedServletContainerCustomizer() {
        return container -> {
            for(;;){
                int port = RandomUtils.nextInt(PORT_RANGE_START, PORT_RANGE_END);
                try {
                    ServerSocket ss = new ServerSocket(port);
                    ss.close();
                } catch (Exception e) {
                    log.error("customize error",e);
                    continue;
                }
                container.setPort(port);
                break;
            }
        };
    }
}
