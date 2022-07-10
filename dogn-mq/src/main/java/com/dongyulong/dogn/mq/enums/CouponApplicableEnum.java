package com.dongyulong.dogn.mq.enums;

/**
 * @Author 王雪源
 * @Date 2022/3/9 11:14
 * @Version 1.0
 */
public enum CouponApplicableEnum {

    /**
     * 乘客
     */
    passenger((byte)1),
    /**
     * 司机
     */
    taxi((byte)3),
    /**
     * 车主
     */
    driver((byte)2);

    private byte applicable;

    public byte getApplicable() {
        return applicable;
    }

    public void setApplicable(byte applicable) {
        this.applicable = applicable;
    }

    CouponApplicableEnum(byte applicable) {
        this.applicable = applicable;
    }
}
