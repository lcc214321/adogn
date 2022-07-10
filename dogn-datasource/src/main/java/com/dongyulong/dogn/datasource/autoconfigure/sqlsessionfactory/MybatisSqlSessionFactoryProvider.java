package com.dongyulong.dogn.datasource.autoconfigure.sqlsessionfactory;

import cn.hutool.core.util.ArrayUtil;
import com.dongyulong.dogn.datasource.autoconfigure.DynamicDataSourceProperties;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author dongy
 * @date 16:07 2022/1/20
 **/
@Configuration
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "autoOrm", havingValue = "MYBATIS")
@ConditionalOnClass({MybatisAutoConfiguration.class, MybatisProperties.class, ConfigurationCustomizer.class})
@EnableConfigurationProperties(MybatisProperties.class)
public class MybatisSqlSessionFactoryProvider implements SqlSessionFactoryProvider {

    private static final List<String> DEFAULT_MAPPER_LOCATIONS = new ArrayList<>(Collections.singletonList("classpath*:/mapper/**/*.xml"));

    private final MybatisProperties properties;

    private final ObjectProvider<Interceptor[]> interceptorsProvider;

    private final ObjectProvider<TypeHandler[]> typeHandlersProvider;

    private final ObjectProvider<LanguageDriver[]> languageDriversProvider;

    private final ResourceLoader resourceLoader;

    private final ObjectProvider<DatabaseIdProvider> databaseIdProvider;

    private final ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider;

    private final MybatisAutoConfiguration mybatisAutoConfiguration;

    public MybatisSqlSessionFactoryProvider(MybatisProperties properties,
                                            ObjectProvider<Interceptor[]> interceptorsProvider,
                                            ObjectProvider<TypeHandler[]> typeHandlersProvider,
                                            ObjectProvider<LanguageDriver[]> languageDriversProvider,
                                            ResourceLoader resourceLoader,
                                            ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                            ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        this.properties = properties;
        this.interceptorsProvider = interceptorsProvider;
        this.typeHandlersProvider = typeHandlersProvider;
        this.languageDriversProvider = languageDriversProvider;
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider;
        this.configurationCustomizersProvider = configurationCustomizersProvider;
        mybatisAutoConfiguration = new MybatisAutoConfiguration(this.properties,
                this.interceptorsProvider,
                this.typeHandlersProvider,
                this.languageDriversProvider,
                this.resourceLoader,
                this.databaseIdProvider,
                this.configurationCustomizersProvider);
    }


    @Override
    public SqlSessionFactory buildSqlSessionFactory(DataSource dataSource) throws IllegalAccessException {
        if (ArrayUtil.isNotEmpty(properties.getMapperLocations())) {
            DEFAULT_MAPPER_LOCATIONS.addAll(Arrays.asList(properties.getMapperLocations()));
        }
        properties.setMapperLocations(DEFAULT_MAPPER_LOCATIONS.toArray(new String[0]));
        mybatisAutoConfiguration.afterPropertiesSet();
        try {
            return mybatisAutoConfiguration.sqlSessionFactory(dataSource);
        } catch (Exception e) {
            throw new IllegalAccessException("buildSqlSessionFactory fail" + e.getMessage());
        }
    }

    @Override
    public SqlSessionTemplate buildSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return mybatisAutoConfiguration.sqlSessionTemplate(sqlSessionFactory);
    }

}

