package com.dongyulong.dogn.datasource.toolkit;

import com.dongyulong.dogn.datasource.enums.IDatabase;
import com.dongyulong.dogn.datasource.routing.Routing;
import com.dongyulong.dogn.datasource.entities.TargetDataSource;
import com.dongyulong.dogn.datasource.enums.DatabaseEnum;
import com.dongyulong.dogn.datasource.enums.DatabaseTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * @author dongy
 * @version v2.0.1
 * @date 15:03 2022/1/5
 **/
@Slf4j
public class DynamicDataSourceContextHolder {

    /**
     * 存放当前线程使用的数据源类型信息
     */
    private static final ThreadLocal<TargetDataSource> CONTEXT_HOLDER = ThreadLocal.withInitial(TargetDataSource::buildDefault);

    /**
     * 修改数据库类型
     *
     * @param dataSourceType -
     */
    public static void modifyDatabaseType(DatabaseTypeEnum dataSourceType) {
        TargetDataSource targetDataSource = getDataSourceType();
        setDataSourceType(dataSourceType, targetDataSource.getDatabase(), targetDataSource.getRouting());
    }

    /**
     * 设置数据源(carpool/taxi不可使用此方法)
     *
     * @param dataSourceType 数据库类型
     * @param databaseName   数据库名称，如 carpool
     */
    public static void setDataSourceType(DatabaseTypeEnum dataSourceType, IDatabase databaseName) {
        Assert.isTrue(!StringUtils.equalsAny(databaseName.name(), DatabaseEnum.carpool.name(), DatabaseEnum.taxi.name()), "当前方式不支持carpool、taxi");
        setDataSourceType(dataSourceType, databaseName, Routing.DEFAULT);
    }

    /**
     * 设置数据源
     *
     * @param dataSourceType -
     * @param databaseName   -
     */
    public static void setDataSourceType(DatabaseTypeEnum dataSourceType, IDatabase databaseName, Routing routing) {
        TargetDataSource targetDataSource = TargetDataSource.builder().databaseType(dataSourceType).database(databaseName).routing(routing).build();
        log.debug("setDataSourceType ========================>targetDataSource:{}", targetDataSource);
        CONTEXT_HOLDER.set(targetDataSource);
    }

    /**
     * 设置出租车数据源(根据taxiOrderId,taxiRideId做路由)
     * 设置顺风车数据源(根据userOrderId,rideId做路由)
     *
     * @param id             taxiOrderId或taxiRideId、userOrderId或 rideId
     * @param dataSourceType -
     */
    public static void setDataSourceType(DatabaseTypeEnum dataSourceType, DatabaseEnum databaseName, Long id) {
        setDataSourceType(dataSourceType, databaseName, Routing.build(id));
    }

    /**
     * 获取数据源
     *
     * @return -
     */
    public static TargetDataSource getDataSourceType() {
        log.debug("getDataSourceType ========================>{}", CONTEXT_HOLDER.get());
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除数据源
     */
    public static void clearDataSourceType() {
        log.debug("clearDataSourceType ========================>");
        CONTEXT_HOLDER.remove();
    }


}
