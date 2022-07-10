package com.dongyulong.dogn.datasource.routing.eg;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
//import com.didapinche.agaue.datasource.common.RouteNotFoundException;
//import com.didapinche.thrift.routing.RETURN_CODE;
//import com.didapinche.thrift.routing.RoutingThriftService;
//import com.didapinche.thrift.routing.TResult;
import com.dongyulong.dogn.common.exception.DException;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.datasource.common.RouteNotFoundException;
import com.dongyulong.dogn.datasource.enums.DatabaseEnum;
import com.dongyulong.dogn.datasource.enums.IDatabase;
import com.dongyulong.dogn.datasource.routing.IShardingRoutingRule;
import com.dongyulong.dogn.datasource.routing.Routing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author dongy
 * @version v2.0.1
 * @date 12:20 2022/1/5
 **/
@Slf4j
@Configuration
@ConditionalOnExpression("T(org.apache.commons.lang3.StringUtils).contains('${spring.datasource.multi.enable.dataSourceName}',T(com.didapinche.agaue.datasource.enums.DatabaseEnum).order.name())")
public class DefaultShardingRoutingRule implements IShardingRoutingRule {

//    @Resource
//    private RoutingThriftService.Iface routingThriftService;

    private static final String DEFAULT_SUFFIX = "0";
    private static final int DEFAULT_SUFFIX_INT = 0;

    private String routingById(long rideId) throws RouteNotFoundException {
        try {
//            TResult result = routingThriftService.getRoutingById(null, rideId);
//            if (result == null || result.getCode() != RETURN_CODE.SUCC.getValue()) {
//                throw new IllegalAccessException("Invalid return! " + JSON.toJSONString(result));
//            }
//            return result.getLookupKey();
            return String.valueOf(RandomUtil.randomInt());
        } catch (Exception e) {
            log.error("Invoke routing-thrift-service error! params:, {}", rideId, e);
            throw new RouteNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public String routing(Routing routing) {
        if (routing == null) {
            throw new DException(ErrorCode.ROUTING_ERROR);
        }
        //如果id未null或0则默认路由到0库
        Long id = routing.getId();
        if (id == null || id == 0) {
            return null;
        }
        return routingById(id);
    }

    @Override
    public IDatabase[] apply() {
        return new IDatabase[]{
                DatabaseEnum.carpool, DatabaseEnum.taxi, DatabaseEnum.order
        };
    }
}
