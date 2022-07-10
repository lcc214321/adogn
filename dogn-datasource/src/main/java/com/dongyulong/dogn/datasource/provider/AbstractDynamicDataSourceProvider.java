package com.dongyulong.dogn.datasource.provider;

import com.alibaba.druid.pool.DruidDataSource;
import com.dongyulong.dogn.datasource.common.DataSourceCommonProperties;
import com.dongyulong.dogn.datasource.common.DataSourceConnectionProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author dongy
 * @version v2.0.1
 * @date 13:15 2022/1/6
 **/
@Slf4j
@ComponentScan("com.didapinche")
public abstract class AbstractDynamicDataSourceProvider {

    protected static final String MASTER_SUFFIX = "%s_master";
    protected static final String SLAVE_SUFFIX = "%s_slave";

    /**
     * 加载数据源信息
     *
     * @return -
     */
    public abstract Map<String, DataSource> loadDataSources();

    public DruidDataSource build(DataSourceConnectionProperties connectionProperties, DataSourceCommonProperties commonProperties) {

        DruidDataSource datasource = new DruidDataSource();

        //connection config
        datasource.setUrl(connectionProperties.getUrl());
        datasource.setUsername(connectionProperties.getUsername());
        datasource.setPassword(connectionProperties.getPassword());

        //common config
        datasource.setDriverClassName(commonProperties.getDriverClassName());
        datasource.setInitialSize(commonProperties.getInitialSize());
        datasource.setMinIdle(commonProperties.getMinIdle());
        datasource.setMaxActive(commonProperties.getMaxActive());
        datasource.setMaxWait(commonProperties.getMaxWait());

        if (commonProperties.getDefaultAutoCommit() != null) {
            datasource.setDefaultAutoCommit(commonProperties.getDefaultAutoCommit());
        }
        datasource.setRemoveAbandoned(commonProperties.isRemoveAbandoned());
        if (commonProperties.getRemoveAbandonedTimeout() > 0) {
            datasource.setRemoveAbandonedTimeout(commonProperties.getRemoveAbandonedTimeout());
        }
        datasource.setTimeBetweenEvictionRunsMillis(commonProperties.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(commonProperties.getMinEvictableIdleTimeMillis());
        if (StringUtils.isNotEmpty(commonProperties.getValidationQuery())) {
            datasource.setValidationQuery(commonProperties.getValidationQuery());
        }
        datasource.setTestWhileIdle(commonProperties.isTestWhileIdle());
        datasource.setTestOnBorrow(commonProperties.isTestOnBorrow());
        datasource.setTestOnReturn(commonProperties.isTestOnReturn());
        try {
            datasource.setFilters(commonProperties.getFilters());
        } catch (SQLException e) {
            log.error("DruidDataSourceBuilder druid configuration initialization filter failed.", e);
        }
        datasource.setConnectionProperties(commonProperties.getConnectionProperties());
        datasource.setConnectionInitSqls(commonProperties.getConnectionInitSqls());

        return datasource;
    }

}
