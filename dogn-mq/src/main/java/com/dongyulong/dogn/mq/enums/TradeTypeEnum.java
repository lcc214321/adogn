package com.dongyulong.dogn.mq.enums;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author 王雪源
 * @Date 2021/12/10 15:09
 * @Version 1.0
 */
public enum TradeTypeEnum {
    /**
     * 转账
     */
    transfer(1),
    /**
     * 充值
     */
    recharge(2),
    /**
     * 提现
     */
    withdraw(3),
    /**
     * 退款
     */
    refund(4),
    /**
     * 支付
     */
    payment(5),
    /**
     * 没收
     */
    confiscate(6),
    /**
     * 系统处理（非常规操作，用于特殊业务统计）
     */
    system(7);

    private int tradeType;

    public int getTradeType() {
        return tradeType;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }

    TradeTypeEnum(int tradeType) {
        this.tradeType = tradeType;
    }

    private static Map<String, TradeTypeEnum> tradeTypeEnumMap = new HashMap<>();
    private static Set<Integer> tradeTypeSet = new HashSet<>();
    static{
        TradeTypeEnum[] values = TradeTypeEnum.values();
        for (TradeTypeEnum tradeTypeEnum: values){
            tradeTypeEnumMap.put(tradeTypeEnum.name(),tradeTypeEnum);
            tradeTypeSet.add(tradeTypeEnum.getTradeType());
        }
    }

    public static TradeTypeEnum getTypeEnum(String tradeType){
        return tradeTypeEnumMap.get(tradeType);
    }

    public static boolean validateType(Integer tradeType){
        return tradeTypeSet.contains(tradeType);
    }
}
