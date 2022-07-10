package com.dongyulong.dogn.datasource.autoconfigure;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.dongyulong.dogn.datasource.common.CommonPropertiesEntity;
import com.dongyulong.dogn.datasource.common.ConnectionPropertiesEntity;
import com.dongyulong.dogn.datasource.common.ShardingConnectionProperties;
import com.dongyulong.dogn.datasource.enums.DatabaseEnum;
import com.dongyulong.dogn.datasource.plugin.AutofillCreationHandler;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 公共配置
 *
 * @author dongy
 * @version v2.0.1
 * @date 10:16 2022/1/4
 **/
@Configuration("dynamicDataSourceProperties")
@AutoConfigureOrder(-100)
@ConfigurationProperties(prefix = DynamicDataSourceProperties.PREFIX)
public class DynamicDataSourceProperties implements InitializingBean {

    public static final String PREFIX = "spring.datasource.multi.enable";
    /**
     * 不需要的数据源链接
     */
    private static final List<String> UNWANTED_DATA_SOURCE_LINK = Arrays.asList("common", "config", "voice", DatabaseEnum.carpool.name(), DatabaseEnum.taxi.name());


    @Getter
    @Setter
    private Set<String> dataSourceName;

    @Getter
    @Setter
    private Boolean all = Boolean.FALSE;

    @Getter
    @Setter
    private String autoOrm;

    /**
     * 是否开启mybatis动态表名插件
     * 暂未验证mybatis是否可用
     */
    @Getter
    @Setter
    private Boolean dynamicTableName;

    /**
     * 是否开启mybatis自动填充时间
     * 只支持创建时间和修改时间
     * 暂未验证mybatis是否可用
     *
     * @see AutofillCreationHandler
     */
    @Getter
    @Setter
    private Boolean autofillDate;
    /**
     * 创建时间属性名
     * 暂未验证mybatis是否可用
     *
     * @see AutofillCreationHandler
     */
    @Getter
    @Setter
    private String createTimeFieldName;
    /**
     * 修改时间属性名
     * 暂未验证mybatis是否可用
     *
     * @see AutofillCreationHandler
     */
    @Getter
    @Setter
    private String updateTimeFieldName;


    @Getter
    private CommonPropertiesEntity dataSourceProperties;
    /**
     * datasource.enterprise.master.url
     * key:enterprise
     */
    private final Map<String, ConnectionPropertiesEntity> datasourceConnect = new LinkedHashMap<>(32);
    /**
     * datasource.taxi_sharding.shards.3
     * key:taxi
     */
    private final Map<String, ShardingConnectionProperties> shardingDatasourceConnect = new LinkedHashMap<>(32);
    /**
     * 分片数据源后缀,key:数据库名,value:当前分片数据源所有的分片后缀
     * datasource.taxi_sharding.shards.3
     * key:taxi
     * value:{"3"...}
     */
    @Getter
    private final Map<String, List<String>> datasourceShardingSuffix = new LinkedHashMap<>(32);
    /**
     * 所有配置的数据库名
     */
    private Set<String> allDatabase;

    @Bean("datasourceConnect")
    @Order(-1000)
    @ConfigurationProperties(prefix = "datasource")
    public Map<String, Object> datasourceConnect() {
        return new LinkedHashMap<>(32);
    }

    @Bean("dataSourceProperties")
    @Order(-1000)
    @ConfigurationProperties(prefix = "datasource.common")
    public CommonPropertiesEntity dataSourceProperties() {
        return new CommonPropertiesEntity();
    }

    @Override
    @DependsOn({"datasourceConnect", "dataSourceProperties"})
    public void afterPropertiesSet() throws Exception {
        //初始化apollo数据
        this.dataSourceProperties = dataSourceProperties();
        Map<String, Object> allDatasourceConnect = datasourceConnect();
        Assert.notNull(allDatasourceConnect, "获取apollo数据源链接失败");
        this.allDatabase = new HashSet<>();
        allDatasourceConnect.forEach((datasourceKey, config) -> {
            if (UNWANTED_DATA_SOURCE_LINK.contains(datasourceKey)) {
                return;
            }
            if (StringUtils.startsWithAny(datasourceKey, DatabaseEnum.carpool.name(), DatabaseEnum.taxi.name(), DatabaseEnum.order.name())) {
                String database = StringUtils.substring(datasourceKey, 0, StringUtils.indexOf(datasourceKey, "_"));
                ShardingConnectionProperties shardingConnectionProperties = BeanUtil.copyProperties(config, ShardingConnectionProperties.class);
                Map<String, String> shards = shardingConnectionProperties.getShards();
                this.addShardingSuffix(database, shards.keySet());
                this.allDatabase.add(database);
                this.shardingDatasourceConnect.put(database, shardingConnectionProperties);
                return;
            }
            ConnectionPropertiesEntity connectionProperties = BeanUtil.copyProperties(config, ConnectionPropertiesEntity.class);
            String database = StringUtils.replace(datasourceKey, "_", StringUtils.EMPTY);
            this.allDatabase.add(database);
            this.datasourceConnect.put(database, connectionProperties);
        });
        //配置校验
        if (!this.getAll() && CollectionUtils.isEmpty(this.getDataSourceName())) {
            throw new IllegalAccessException("请指定spring.datasource.multi.enable.all为true或配置spring.datasource.multi.enable.dataSourceName");
        }
        //检查spring.datasource.multi.enable.dataSourceName配置数据是否正确
        if (!CollectionUtils.isEmpty(this.getDataSourceName())) {
            for (String name : this.getDataSourceName()) {
                if (!this.allDatabase.contains(name)) {
                    throw new IllegalAccessException("spring.datasource.multi.enable.dataSourceName配置错误[" + name + "] case:" + JSON.toJSONString(this.allDatabase));
                }
            }
        }

    }

    private void addShardingSuffix(String database, Set<String> shardingSuffixs) {
        List<String> shardingSuffixList = this.computeIfAbsent(database);
        shardingSuffixList.addAll(shardingSuffixs);
    }

    public List<String> computeIfAbsent(String database) {
        return this.datasourceShardingSuffix.computeIfAbsent(database, s -> new ArrayList<>());
    }

    public Map<String, ConnectionPropertiesEntity> getEnableDatasourceConnect() {
        if (CollectionUtils.isEmpty(this.datasourceConnect)) {
            return Collections.emptyMap();
        }
        Set<String> enableDatabases = getEnableDataSourceName();
        Map<String, ConnectionPropertiesEntity> enableDatasourceConnectMap = new HashMap<>(enableDatabases.size() * 4);
        this.datasourceConnect.forEach((database, datasourceProperties) -> {
            if (enableDatabases.contains(database)) {
                enableDatasourceConnectMap.put(database, datasourceProperties);
            }
        });
        return enableDatasourceConnectMap;
    }

    public Map<String, ShardingConnectionProperties> getEnableShardingDatasourceConnect() {
        if (CollectionUtils.isEmpty(this.shardingDatasourceConnect)) {
            return Collections.emptyMap();
        }
        Set<String> enableDatabases = getEnableDataSourceName();
        Map<String, ShardingConnectionProperties> enableDatasourceConnectMap = new HashMap<>(enableDatabases.size() * 4);
        this.shardingDatasourceConnect.forEach((database, datasourceProperties) -> {
            if (enableDatabases.contains(database)) {
                enableDatasourceConnectMap.put(database, datasourceProperties);
            }
        });
        return enableDatasourceConnectMap;
    }

    private Set<String> getEnableDataSourceName() {
        if (this.all) {
            return this.allDatabase;
        }
        return this.dataSourceName;
    }
}
