package com.dongyulong.dogn.datasource.provider;

import com.dongyulong.dogn.datasource.autoconfigure.DynamicDataSourceProperties;
import com.dongyulong.dogn.datasource.common.CommonPropertiesEntity;
import com.dongyulong.dogn.datasource.common.ConnectionPropertiesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dongy
 * @version v2.0.1
 * @date 13:41 2022/1/6
 **/
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@ConditionalOnBean(DynamicDataSourceProperties.class)
public class DefaultDataSourceProvider extends AbstractDynamicDataSourceProvider {

    private final Map<String, ConnectionPropertiesEntity> connectionPropertiesEntityMap;
    private final CommonPropertiesEntity dataSourceProperties;

    @Autowired
    public DefaultDataSourceProvider(DynamicDataSourceProperties properties) {
        this.connectionPropertiesEntityMap = properties.getEnableDatasourceConnect();
        this.dataSourceProperties = properties.getDataSourceProperties();
    }

    @Override
    public Map<String, DataSource> loadDataSources() {
        if (CollectionUtils.isEmpty(this.connectionPropertiesEntityMap)) {
            return Collections.emptyMap();
        }
        Map<String, DataSource> dataSourceMap = new HashMap<>(this.connectionPropertiesEntityMap.size() * 4);
        connectionPropertiesEntityMap.forEach((database, dataSourceConnection) -> {
            String masterDataSourceKey = String.format(MASTER_SUFFIX, database);
            String slaveDataSourceKey = String.format(SLAVE_SUFFIX, database);
            dataSourceMap.put(masterDataSourceKey, super.build(dataSourceConnection.getMaster(), dataSourceProperties.getMaster()));
            dataSourceMap.put(slaveDataSourceKey, super.build(dataSourceConnection.getSlave(), dataSourceProperties.getSlave()));
        });
        return dataSourceMap;
    }
}
