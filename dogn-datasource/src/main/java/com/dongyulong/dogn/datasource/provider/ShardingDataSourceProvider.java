package com.dongyulong.dogn.datasource.provider;

import com.dongyulong.dogn.datasource.autoconfigure.DynamicDataSourceProperties;
import com.dongyulong.dogn.datasource.common.CommonPropertiesEntity;
import com.dongyulong.dogn.datasource.common.DataSourceConnectionProperties;
import com.dongyulong.dogn.datasource.common.ShardingConnectionProperties;
import com.dongyulong.dogn.datasource.strategy.ShardingRoutingStrategy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dongy
 * @version v2.0.1
 * @date 14:22 2022/1/6
 **/
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@ConditionalOnBean(DynamicDataSourceProperties.class)
public class ShardingDataSourceProvider extends AbstractDynamicDataSourceProvider {

    private final Map<String, ShardingConnectionProperties> stringShardingConnectionPropertiesMap;
    private final CommonPropertiesEntity dataSourceProperties;
    private final ShardingRoutingStrategy shardingRoutingStrategy;
    private final Map<String, List<String>> datasourceShardingSuffix;

    @Autowired
    public ShardingDataSourceProvider(DynamicDataSourceProperties properties,
                                      ObjectProvider<ShardingRoutingStrategy> objectProvider) {
        this.stringShardingConnectionPropertiesMap = properties.getEnableShardingDatasourceConnect();
        this.dataSourceProperties = properties.getDataSourceProperties();
        this.shardingRoutingStrategy = objectProvider.getIfAvailable();
        this.datasourceShardingSuffix = properties.getDatasourceShardingSuffix();
    }

    @Override
    public Map<String, DataSource> loadDataSources() {
        if (CollectionUtils.isEmpty(stringShardingConnectionPropertiesMap)) {
            return Collections.emptyMap();
        }
        Map<String, DataSource> dataSourceMap = new HashMap<>(16);
        stringShardingConnectionPropertiesMap.forEach((database, shardingConnectionProperties) -> {
            Set<Map.Entry<String, String>> entries = shardingConnectionProperties.getShards().entrySet();
            for (Map.Entry<String, String> entry : entries) {
                DataSourceConnectionProperties masterProperties = new DataSourceConnectionProperties();
                masterProperties.setPassword(shardingConnectionProperties.getPassword());
                masterProperties.setUsername(shardingConnectionProperties.getUsername());
                masterProperties.setUrl(String.format(shardingConnectionProperties.getUrl(), entry.getValue().split(",")[0], shardingConnectionProperties.getDatabase()));
                String masterDataSourceKey = String.format("%s_%s", String.format(MASTER_SUFFIX, database), entry.getKey());
                dataSourceMap.put(masterDataSourceKey, super.build(masterProperties, dataSourceProperties.getMaster()));
                DataSourceConnectionProperties slaveProperties = new DataSourceConnectionProperties();
                slaveProperties.setPassword(shardingConnectionProperties.getPassword());
                slaveProperties.setUsername(shardingConnectionProperties.getUsername());
                slaveProperties.setUrl(String.format(shardingConnectionProperties.getUrl(), entry.getValue().split(",")[1], shardingConnectionProperties.getDatabase()));
                String slaveDataSourceKey = String.format("%s_%s", String.format(SLAVE_SUFFIX, database), entry.getKey());
                dataSourceMap.put(slaveDataSourceKey, super.build(slaveProperties, dataSourceProperties.getSlave()));
            }
        });
        if (shardingRoutingStrategy != null) {
            shardingRoutingStrategy.loadDatasourceShardingSuffix(datasourceShardingSuffix);
        }
        return dataSourceMap;
    }

}
