package com.dongyulong.dogn.datasource;

import com.dongyulong.dogn.datasource.entities.TargetDataSource;
import com.dongyulong.dogn.datasource.provider.AbstractDynamicDataSourceProvider;
import com.dongyulong.dogn.datasource.strategy.IRoutingStrategy;
import com.dongyulong.dogn.datasource.toolkit.DynamicDataSourceContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * @author dongy
 * @version v2.0.1
 * @date 16:31 2022/1/6
 **/
@ComponentScan("com.dongyulong")
@Configuration
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private static final String ROUTING_STRATEGY_FORMAT = "%s" + IRoutingStrategy.BEAN_SUFFIX;
    private static final String DEFAULT_ROUTING_STRATEGY_BEAN_ID = "default" + IRoutingStrategy.BEAN_SUFFIX;

    /**
     * 路由规则对应数据库名
     */
    private final Map<String, IRoutingStrategy> routingStrategyMap;
    /**
     * 数据库和路由规则关系
     */
    private final IRoutingStrategy defaultRoutingStrategy;

    /**
     * 目标数据源
     */
    public final Map<Object, Object> dataSources = new LinkedHashMap<>(64);

    @Autowired
    public DynamicRoutingDataSource(Map<String, IRoutingStrategy> routingStrategyMap,
                                    List<AbstractDynamicDataSourceProvider> dataSourceProviders) {
        this.routingStrategyMap = routingStrategyMap;
        this.defaultRoutingStrategy = routingStrategyMap.get(DEFAULT_ROUTING_STRATEGY_BEAN_ID);
        this.setTargetDataSources(dataSources);
        if (!CollectionUtils.isEmpty(dataSourceProviders)) {
            dataSourceProviders.stream().map(AbstractDynamicDataSourceProvider::loadDataSources).forEach(this::addDataSources);
        }
    }

    @Override
    protected Object determineCurrentLookupKey() {
        TargetDataSource targetDataSource = DynamicDataSourceContextHolder.getDataSourceType();
        IRoutingStrategy routingStrategy = getRoutingStrategy(targetDataSource.getDatabase().name());
        return routingStrategy.determineKey(targetDataSource);
    }

    private IRoutingStrategy getRoutingStrategy(String databaseName) {
        return routingStrategyMap.getOrDefault(String.format(ROUTING_STRATEGY_FORMAT, databaseName), defaultRoutingStrategy);
    }

    public void addDataSources(Map<String, DataSource> dataSourceMap) {
        if (CollectionUtils.isEmpty(dataSourceMap)) {
            return;
        }
        dataSources.putAll(dataSourceMap);
        this.setTargetDataSources(dataSources);
    }

}
