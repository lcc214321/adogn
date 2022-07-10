package com.dongyulong.dogn.datasource.strategy;

import cn.hutool.core.map.MapUtil;
import com.dongyulong.dogn.common.exception.DException;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.datasource.entities.TargetDataSource;
import com.dongyulong.dogn.datasource.enums.IDatabase;
import com.dongyulong.dogn.datasource.routing.IShardingRoutingRule;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/82:22 下午
 * @since v1.0
 */
@Service
public class ShardingRoutingStrategy implements IRoutingStrategy {

    private final Map<String, IShardingRoutingRule> routingRuleMap = MapUtil.newConcurrentHashMap();
    /**
     * 所有配置的分库后缀
     */
    private final Map<String, List<String>> datasourceShardingSuffix = MapUtil.newConcurrentHashMap();

    private static final String DEFAULT_SUFFIX = "0";

    @Autowired
    public ShardingRoutingStrategy(ObjectProvider<IShardingRoutingRule[]> shardingRoutingProvider) {
        IShardingRoutingRule[] shardingRoutings = shardingRoutingProvider.getIfAvailable();
        if (ArrayUtils.isEmpty(shardingRoutings)) {
            return;
        }
        Arrays.stream(shardingRoutings).forEach(shardingRouting -> {
            IDatabase[] databasesArr = shardingRouting.apply();
            if (ArrayUtils.isEmpty(databasesArr)) {
                return;
            }
            Arrays.stream(databasesArr).forEach(databases -> {
                this.routingRuleMap.put(databases.name(), shardingRouting);
            });
        });
    }

    @Override
    public String determineKey(TargetDataSource targetDataSource) {
        if (MapUtil.isEmpty(routingRuleMap)) {
            throw new DException(ErrorCode.ROUTING_ERROR);
        }
        IShardingRoutingRule shardingRouting = routingRuleMap.get(targetDataSource.getDatabase().name());
        String routingKey = shardingRouting.routing(targetDataSource.getRouting());
        List<String> routingKeyList = datasourceShardingSuffix.get(targetDataSource.getDatabase().name());
        if (routingKey == null || !routingKeyList.contains(routingKey)) {
            routingKey = DEFAULT_SUFFIX;
        }
        return targetDataSource.toString(routingKey);
    }

    @Override
    public final void loadDatasourceShardingSuffix(Map<String, List<String>> datasourceShardingSuffix) {
        if (MapUtil.isEmpty(datasourceShardingSuffix)) {
            return;
        }
        this.datasourceShardingSuffix.putAll(datasourceShardingSuffix);
    }


}
