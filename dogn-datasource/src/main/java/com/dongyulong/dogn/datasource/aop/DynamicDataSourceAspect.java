package com.dongyulong.dogn.datasource.aop;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.dongyulong.dogn.common.exception.DException;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.datasource.annotation.DS;
import com.dongyulong.dogn.datasource.annotation.DSTransactional;
import com.dongyulong.dogn.datasource.routing.Routing;
import com.dongyulong.dogn.datasource.enums.DatabaseEnum;
import com.dongyulong.dogn.datasource.enums.DatabaseTypeEnum;
import com.dongyulong.dogn.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

/**
 * @author dongy
 * @version v2.0.1
 * @date 15:02 2022/1/5
 **/
@Slf4j
@Aspect
@Order(-1)
@AutoConfigureOrder(-1)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DynamicDataSourceAspect {

    /**
     * 设置数据库信息
     *
     * @param point           -
     * @param dsTransactional -
     */
    @Before("@annotation(dsTransactional)")
    public void changeDataSource(JoinPoint point, DSTransactional dsTransactional) {
        extracted(point, dsTransactional.type(), dsTransactional.name(), dsTransactional.index());
    }

    /**
     * 设置数据库信息
     *
     * @param point -
     * @param ds    -
     */
    @Before("@annotation(ds)")
    public void changeDataSource2(JoinPoint point, DS ds) {
        extracted(point, ds.type(), ds.name(), ds.index());
    }

    private void extracted(JoinPoint point, DatabaseTypeEnum dataSourceType, DatabaseEnum databaseName, int index) {
        Object[] args = point.getArgs();
        if (databaseName == DatabaseEnum.carpool || databaseName == DatabaseEnum.taxi || databaseName == DatabaseEnum.order) {
            if (ArrayUtil.isEmpty(args)) {
                throw new DException(ErrorCode.ROUTING_ERROR.getCode(),
                        StrUtil.format("{} 路由失败,数据库didapinche_{}需要路由,至少需要参数:{}", point.getTarget().getClass(), databaseName.name(), Routing.class));
            }
            if (!(args[index] instanceof Routing)) {
                throw new DException(ErrorCode.ROUTING_ERROR.getCode(),
                        StrUtil.format("{} 路由失败,数据库didapinche_{}需要路由,必传参数:{}", point.getTarget().getClass(), databaseName.name(), Routing.class));
            }
            Routing routing = (Routing) args[index];
            if (databaseName == DatabaseEnum.order && routing.getUserId() == null) {
                throw new DException(ErrorCode.ROUTING_ERROR.getCode(),
                        StrUtil.format("{} 路由失败,数据库didapinche_order必须根据userId分表", point.getTarget().getClass()));
            }
            log.debug("changeDataSource ====================> dataSourceType:{},databaseName:{},routing:{}", dataSourceType, databaseName, routing);
            DynamicDataSourceContextHolder.setDataSourceType(dataSourceType, databaseName, routing);
            return;
        }
        log.debug("changeDataSource ====================> dataSourceType:{},databaseName:{}", dataSourceType, databaseName);
        DynamicDataSourceContextHolder.setDataSourceType(dataSourceType, databaseName);
    }

    /**
     * 清除数据库信息
     *
     * @param point -
     * @param ds    -
     */
    @After("@annotation(ds)")
    public void restoreDataSource(JoinPoint point, DS ds) {
        log.debug("restoreDataSource ====================> dataSource:{}", ds);
        DynamicDataSourceContextHolder.clearDataSourceType();
    }

    /**
     * 清除数据库信息
     *
     * @param point           -
     * @param dsTransactional -
     */
    @After("@annotation(dsTransactional)")
    public void restoreDataSource(JoinPoint point, DSTransactional dsTransactional) {
        log.debug("restoreDataSource ====================> dataSource:{}", dsTransactional);
        DynamicDataSourceContextHolder.clearDataSourceType();
    }

    /**
     * 清除数据库信息
     *
     * @param point     -
     * @param ds        -
     * @param exception -
     */
    @AfterThrowing(value = "@annotation(ds)", throwing = "exception")
    public void afterThrowException(JoinPoint point, DS ds, Exception exception) {
        log.debug("afterThrowException ====================> dataSource:{}", ds);
        DynamicDataSourceContextHolder.clearDataSourceType();
    }

    /**
     * 清除数据库信息
     *
     * @param point           -
     * @param dsTransactional -
     * @param exception       -
     */
    @AfterThrowing(value = "@annotation(dsTransactional)", throwing = "exception")
    public void afterThrowException(JoinPoint point, DSTransactional dsTransactional, Exception exception) {
        log.debug("afterThrowException ====================> dataSource:{}", dsTransactional);
        DynamicDataSourceContextHolder.clearDataSourceType();
    }
}
