package com.dongyulong.dogn.mq.enums;

/**
 * @Author 王雪源
 * @Date 2021/12/30 9:55
 * @Version 1.0
 *
 * 消息topic
 */
public enum MQTopicEnum {

    /**
     * thea 共用topic
     */
    t_bdc_payment_thea_settlement,
    /**
     * thea优惠券 共用topic
     */
    t_bdc_payment_thea_coupon_trade,
    /**
     * 第三方支付成功后发送通知
     */
    t_bdc_payment_pay_succ,
    /**
     * 第三方主动查询
     */
    t_bdc_payment_unified_order_create,

    /**
     * 退款成功后发送通知
     */
    t_bdc_payment_refund_succ,
    /**
     * 发起退款的topic
     */
    t_server_trade_refund,
    /**
     * 三化出租车支付
     */
    t_bdc_taxiqr_paid,

    /**
     * third refund order
     */
    t_bdc_payment_refund,
    /**
     * third profit sharing order
     */
    t_bdc_payment_profit_sharing;


    public static final String T_SERVER_TRADE_REFUND = "t_server_trade_refund";
    public static final String T_BDC_PAYMENT_REFUND_SUCC = "t_bdc_payment_refund_succ";
    public static final String T_BDC_TAXIQR_PAID = "t_bdc_taxiqr_paid";
    public static final String T_BDC_PAYMENT_REFUND = "t_bdc_payment_refund";
    public static final String T_BDC_PAYMENT_PROFIT_SHARING = "t_bdc_payment_profit_sharing";
}
