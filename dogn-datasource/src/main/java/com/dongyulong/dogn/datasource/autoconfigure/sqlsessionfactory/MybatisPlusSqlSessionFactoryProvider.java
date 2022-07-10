package com.dongyulong.dogn.datasource.autoconfigure.sqlsessionfactory;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.dongyulong.dogn.datasource.autoconfigure.DynamicDataSourceProperties;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author dongy
 * @date 16:07 2022/1/20
 **/
@Configuration
@ConditionalOnClass({MybatisPlusAutoConfiguration.class, MybatisPlusProperties.class, MybatisPlusPropertiesCustomizer.class})
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "autoOrm", havingValue = "MYBATIS_PLUS")
@EnableConfigurationProperties(MybatisPlusProperties.class)
public class MybatisPlusSqlSessionFactoryProvider implements SqlSessionFactoryProvider {

    private final MybatisPlusProperties properties;
    private final ObjectProvider<Interceptor[]> interceptorsProvider;
    private final ObjectProvider<TypeHandler[]> typeHandlersProvider;
    private final ObjectProvider<LanguageDriver[]> languageDriversProvider;
    private final ResourceLoader resourceLoader;
    private final ObjectProvider<DatabaseIdProvider> databaseIdProvider;
    private final ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider;
    private final ObjectProvider<List<MybatisPlusPropertiesCustomizer>> mybatisPlusPropertiesCustomizerProvider;
    private final ApplicationContext applicationContext;
    private final MybatisPlusAutoConfiguration mybatisPlusAutoConfiguration;

    public MybatisPlusSqlSessionFactoryProvider(MybatisPlusProperties properties,
                                                ObjectProvider<Interceptor[]> interceptorsProvider,
                                                ObjectProvider<TypeHandler[]> typeHandlersProvider,
                                                ObjectProvider<LanguageDriver[]> languageDriversProvider,
                                                ResourceLoader resourceLoader,
                                                ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                                ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                                ObjectProvider<List<MybatisPlusPropertiesCustomizer>> mybatisPlusPropertiesCustomizerProvider,
                                                ApplicationContext applicationContext) {
        this.properties = properties;
        this.interceptorsProvider = interceptorsProvider;
        this.typeHandlersProvider = typeHandlersProvider;
        this.languageDriversProvider = languageDriversProvider;
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider;
        this.configurationCustomizersProvider = configurationCustomizersProvider;
        this.mybatisPlusPropertiesCustomizerProvider = mybatisPlusPropertiesCustomizerProvider;
        this.applicationContext = applicationContext;
        mybatisPlusAutoConfiguration = new MybatisPlusAutoConfiguration(this.properties,
                this.interceptorsProvider,
                this.typeHandlersProvider,
                this.languageDriversProvider,
                this.resourceLoader,
                this.databaseIdProvider,
                this.configurationCustomizersProvider,
                this.mybatisPlusPropertiesCustomizerProvider,
                this.applicationContext);
    }


    @Override
    public SqlSessionFactory buildSqlSessionFactory(DataSource dataSource) throws IllegalAccessException {
        mybatisPlusAutoConfiguration.afterPropertiesSet();
        try {
            return mybatisPlusAutoConfiguration.sqlSessionFactory(dataSource);
        } catch (Exception e) {
            throw new IllegalAccessException("buildSqlSessionFactory fail" + e.getMessage());
        }
    }

    @Override
    public SqlSessionTemplate buildSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return mybatisPlusAutoConfiguration.sqlSessionTemplate(sqlSessionFactory);
    }
}
