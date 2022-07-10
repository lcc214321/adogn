package com.dongyulong.dogn.datasource.routing;

import com.dongyulong.dogn.datasource.enums.IDatabase;

import java.util.function.Function;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/82:18 下午
 * @since v1.0
 */
public interface IShardingRoutingRule {


    /**
     * Applies this function to the given argument.
     *
     * @param routing the function argument
     * @return the function result
     */
    String routing(Routing routing);

    /**
     * 哪些数据库可以使用当前路由规则
     *
     * @return -
     */
    IDatabase[] apply();
}
