package com.dongyulong.dogn.table.id;

import com.dongyulong.dogn.table.eg.DeployEnvEnum;
import com.dongyulong.dogn.table.eg.TableIdEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zhangshaolong
 * @create 2022/2/10
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeIdFactory {

    /**
     * 根据业务类型生成新的交易订单号
     *
     * @param userId      用户id
     * @param tableIdEnum 表枚举
     * @param createTime  创建时间
     * @param sourceId    业务类型
     * @return -
     */
    public static TradeId getTradeNo(long userId, TableIdEnum tableIdEnum, Date createTime, Integer sourceId) {
        long suffix = NoUtils.getTableSuffix(userId);
        String env = System.getProperty("env");
        DeployEnvEnum deployEnvEnum = DeployEnvEnum.valueOf(env);
        long uniqueId = NoUtils.getUniqueId(tableIdEnum.getNo(), suffix, deployEnvEnum);
        String tradeNo = NoUtils.getTradeNo(uniqueId, tableIdEnum, sourceId, createTime);
        TradeId result = new TradeId();
        result.uniqueId = uniqueId;
        result.tradeNo = tradeNo;
        result.suffix = suffix;
        return result;
    }

    /**
     * 三化订单
     * 根据业务类型生成新的交易订单号
     *
     * @param userId      用户id
     * @param tableIdEnum 表枚举
     * @param createTime  创建时间
     * @param sourceId    业务类型
     * @return -
     */
    public static TradeId getTaxiqrTradeNo(long userId, TableIdEnum tableIdEnum, Date createTime, Integer sourceId) {
        long suffix = NoUtils.getTableSuffix(userId);
        String env = System.getProperty("env");
        DeployEnvEnum deployEnvEnum = DeployEnvEnum.valueOf(env);
        long uniqueId = NoUtils.getUniqueId(tableIdEnum.getNo(), suffix, deployEnvEnum);
        String tradeNo = NoUtils.getTradeNoTaxiqr(uniqueId, tableIdEnum, sourceId, createTime);
        TradeId result = new TradeId();
        result.uniqueId = uniqueId;
        result.tradeNo = tradeNo;
        result.suffix = suffix;
        return result;
    }

    /**
     * 根据业务类型生成新的交易订单号
     * 当前方法分表后缀为999,给不按userId分表的业务使用
     *
     * @param tableIdEnum 表枚举
     * @param createTime  创建时间，会格式化为yyyyMMdd生成交易订单的前缀
     * @param sourceId    业务类型
     * @return -
     */
    public static TradeId generateTradeNo(TableIdEnum tableIdEnum, Date createTime, Integer sourceId) {
        String env = System.getProperty("env");
        DeployEnvEnum deployEnvEnum = DeployEnvEnum.valueOf(env);
        long uniqueId = NoUtils.getUniqueId(tableIdEnum.getNo(), 999, deployEnvEnum);
        String tradeNo = NoUtils.getTradeNoTaxiqr(uniqueId, tableIdEnum, sourceId, createTime);
        TradeId result = new TradeId();
        result.uniqueId = uniqueId;
        result.tradeNo = tradeNo;
        return result;
    }

    /**
     * 根据原有交易订单号生成新的交易订单号
     *
     * @param suffix      表前缀
     * @param tableIdEnum 表枚举
     * @param createTime  创建时间
     * @param orderNo     订单交易号
     * @return -
     */
    public static TradeId getTradeNoByTradeNo(long suffix, TableIdEnum tableIdEnum, Date createTime, String orderNo) {
        String env = System.getProperty("env");
        DeployEnvEnum deployEnvEnum = DeployEnvEnum.valueOf(env);
        long uniqueId = NoUtils.getUniqueId(tableIdEnum.getNo(), suffix, deployEnvEnum);
        String tradeNo = NoUtils.genTradeNoByOrderNo(uniqueId, tableIdEnum, orderNo, createTime);
        TradeId result = new TradeId();
        result.uniqueId = uniqueId;
        result.tradeNo = tradeNo;
        result.suffix = suffix;
        return result;
    }

    @Getter
    public static class TradeId {
        /**
         * 唯一id
         */
        private long uniqueId;
        /**
         * 交易订单号（支付、退款、分账等商户侧订单号）
         */
        private String tradeNo;
        /**
         * 表前缀
         */
        private long suffix;
    }
}
