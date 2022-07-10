package com.dongyulong.dogn.datasource.strategy;

import com.dongyulong.dogn.datasource.entities.TargetDataSource;

import java.util.List;
import java.util.Map;

/**
 * The interface of dynamic datasource switch strategy
 *
 * @author dongy
 * @version v2.0.1
 * @date 10:49 2022/1/5
 **/
public interface IRoutingStrategy {

    String BEAN_SUFFIX = "RoutingStrategy";

    /**
     * determine a database from the given dataSources
     *
     * @param targetDataSource given dataSources
     * @return final dataSource
     */
    String determineKey(TargetDataSource targetDataSource);

    /**
     * 加载数据源分片后缀
     *
     * @param datasourceShardingSuffix -
     */
    default void loadDatasourceShardingSuffix(Map<String, List<String>> datasourceShardingSuffix) {

    }
}
