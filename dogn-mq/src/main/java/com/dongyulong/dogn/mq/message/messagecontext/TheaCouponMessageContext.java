package com.dongyulong.dogn.mq.message.messagecontext;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author 王雪源
 * @Date 2022/03/08 9:56
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TheaCouponMessageContext extends BaseContext {

    /**
     * 购买券包订单id
     */
    private String orderCid;

    /**
     * 商品id（必填）
     */
    private Long goodsId;

    /**
     * 商品价格（购买时必填）
     */
    private BigDecimal price;

    /**
     * 商品名称（购买时必填）
     */
    private String goodsName;

    /**
     * 包含券集合（必填）
     */
    private List<CouponSet> couponSet;
    /**
     * 包含无忧卡集合（必填）
     */
    private List<CarefreeSet> carefreeSet;
    /**
     * 购买/分发/核销/作废日期
     */
    private Date tradeDate;

    /**
     * 购买支付日期
     */
    private Date payDate;

    /**
     * 操作类型（必填）@see {@link com.didapinche.finance.mq.context.enums.CouponTradeTypeEnum}
     */
    private Byte tradeType;

    /**
     * 优惠券实体
     */
    @Data
    public static class CouponSet{
        /**
         * setId （必填）
         */
        private Long couponSetId;
        /**
         * 优惠券类型（购买时必填） 打折券/抵扣券/车主现金券
         */
        private Integer couponType;
        /**
         * 优惠金额或者补贴金额（打折券填最高价格）（购买时必填）
         */
        private BigDecimal discount;
        /**
         * 折扣 （例如 3折）（选填）
         */
        private BigDecimal discountNum;
        /**
         * 实际抵扣金额（核销券时必填）
         */
        private BigDecimal realDiscount;
        /**
         * 使用人角色(必填) @see {@link com.didapinche.finance.mq.context.enums.CouponApplicableEnum}
         */
        private Byte applicable;
    }


    /**
     * 无忧卡实体
     */
    @Data
    public static class CarefreeSet{
        /**
         * 无忧卡补贴最高金额（购买时必填）
         */
        private BigDecimal discount;
        /**
         * 实际抵扣金额（核销时必填）
         */
        private BigDecimal realDiscount;
        /**
         * 无忧卡数量（必填）
         */
        private Integer amount;
    }

    public void addCouponSet(CouponSet data){
        if(couponSet == null){
            couponSet = new LinkedList<>();
        }
        couponSet.add(data);
    }

    public void addCarefreeSet(CarefreeSet data){
        if(carefreeSet == null){
            carefreeSet = new LinkedList<>();
        }
        carefreeSet.add(data);
    }
}
