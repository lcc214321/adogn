package com.dongyulong.dogn.autoconfigure.monitor.mybatis;

import org.apache.ibatis.plugin.Interceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author dongy
 * @date 15:40 2022/3/15
 **/
@Configuration
@ConditionalOnClass(Interceptor.class)
@ConditionalOnMissingBean(SqlStatementInterceptor.class)
public class SqlStatementInterceptorConfig {

    @Bean
    @Primary
    public SqlStatementInterceptor sqlStatementInterceptor() {
        return new SqlStatementInterceptor();
    }
}
