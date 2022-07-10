package com.dongyulong.dogn.mq.enums;


/**
 * @Author 王雪源
 * @Date 2022/03/08 15:09
 * @Version 1.0
 */
public enum CouponTradeTypeEnum {
    /**
     * 购买（发券）
     */
    distribute((byte)7,"发券"),
    /**
     * 核销
     */
    verification((byte)1,"核销"),
    /**
     * cms客服手动作废（退款作废）
     */
    artificial((byte)2,"作废"),
    /**
     * 过期
     */
    overdue((byte)3,"作废"),
    /**
     * 核销后退回
     */
    verification_recover((byte)4,"恢复"),

    /**
     * 核销后退回，但是退回的时候券已过期
     */
    refund_overdue((byte)5,"恢复并作废"),
    /**
     * 非当月退款
     */
    before_month_refund((byte)6,"非当月退款"),

    /**
     * 作废后退回
     */
    overdue_recover((byte)8,"恢复"),


    /**
     * 券包购买
     */
    pay((byte)10,"购买"),

    /**
     * 券包退款
     */
    refund((byte)9,"退款");

    private byte tradeType;
    private String message;

    public byte getTradeType() {
        return tradeType;
    }

    public void setTradeType(byte tradeType) {
        this.tradeType = tradeType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    CouponTradeTypeEnum(byte tradeType, String message) {
        this.message = message;
        this.tradeType = tradeType;
    }


}
