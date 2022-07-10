package com.dongyulong.dogn.datasource.autoconfigure;

import com.dongyulong.dogn.datasource.DynamicRoutingDataSource;
import com.dongyulong.dogn.datasource.autoconfigure.sqlsessionfactory.SqlSessionFactoryProvider;
import com.dongyulong.dogn.datasource.enums.DatabaseTypeEnum;
import com.dongyulong.dogn.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;


/**
 * @author dongy
 * @version v2.0.1
 * @date 11:43 2022/1/6
 **/
@Slf4j
@ComponentScan("com.dongyulong")
@Configuration
public class DynamicDataSourceAutoConfiguration {

    private final Interceptor[] interceptors;
    private final TypeHandler[] typeHandlers;


    @Autowired
    public DynamicDataSourceAutoConfiguration(ObjectProvider<Interceptor[]> interceptorsProvider,
                                              ObjectProvider<TypeHandler[]> typeHandlersProvider) {
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.typeHandlers = typeHandlersProvider.getIfAvailable();

    }


    @Bean("sqlSessionFactory")
    @Primary
    @Order(2)
    public SqlSessionFactory sqlSessionFactory(SqlSessionFactoryProvider sqlSessionFactoryProvider,
                                               DynamicRoutingDataSource dataSource) throws Exception {
        return sqlSessionFactoryProvider.buildSqlSessionFactory(dataSource);
    }


    @Bean
    @Order(2)
    @Primary
    public DataSourceTransactionManager transactionManager(DynamicRoutingDataSource dynamicRoutingDataSource) {
        return new DataSourceTransactionManager(dynamicRoutingDataSource) {

            @Override
            protected void doBegin(Object transaction, TransactionDefinition definition) {
                if (definition.isReadOnly()) {
                    DynamicDataSourceContextHolder.modifyDatabaseType(DatabaseTypeEnum.slave);
                } else {
                    DynamicDataSourceContextHolder.modifyDatabaseType(DatabaseTypeEnum.master);
                }
                super.doBegin(transaction, definition);
            }

            @Override
            protected void doCleanupAfterCompletion(Object transaction) {
                super.doCleanupAfterCompletion(transaction);
            }
        };
    }
}
