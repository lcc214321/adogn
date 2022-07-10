package com.dongyulong.dogn.mq.enums;

/**
 * @Author 王雪源
 * @Date 2021/12/30 9:55
 * @Version 1.0
 * <p>
 * 消费的group
 */
public enum MQGroupEnum {


    /**
     * thea 数据结算
     */
    C_BDC_PAYMENT_THEA_SETTLEMENT,

    /**
     * Thea 优惠券结算
     */
    C_BDC_PAYMENT_THEA_COUPON_TRADE,

    /**
     * 订阅第三方支付通知
     */
    C_BDC_PAYMENT_PAY_SUCC_NOTIFY,

    /**
     * 三方的订单check
     */
    C_BDC_PAYMENT_UNIFIED_ORDER_STATUS_SYNC3RD,

    /**
     * 退款
     */
    C_BDC_PAYMENT_REFUND,
    /**
     * 智慧码订单分账
     */
    C_BDC_PAYMENT_TAXIQR_PROFIT_SHARING;

    public static final String GROUP_C_BDC_PAYMENT_TAXIQR_PROFIT_SHARING = "C_BDC_PAYMENT_TAXIQR_PROFIT_SHARING";
    public static final String GROUP_C_BDC_PAYMENT_REFUND = "C_BDC_PAYMENT_REFUND";

}
