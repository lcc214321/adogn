package com.dongyulong.dogn.mq.enums;

import java.util.Arrays;

/**
 * @Author 王雪源
 * @Date 2021/12/20 14:06
 * @Version 1.0
 *
 *  实际支付类型
 */
public enum PayTypeEnum {

    /**
     *  H5
     */
    H5("h5"),
    /**
     * 支付宝
     */
    APP("app"),
    /**
     *  小程序
     */
    MINI("mini"),
    /**
     * 系统操作
     */
    SYSTEM("system"),
    /**
     * 未知
     */
    DEFAULT("default");

    private String payType;

    private PayTypeEnum(String payType) {
        this.payType = payType;
    }

    public static PayTypeEnum getType(String type) {
        return Arrays.stream(PayTypeEnum.values())
                .filter(typeEnum -> typeEnum.getPayType().equalsIgnoreCase(type))
                .findAny()
                .orElse(DEFAULT);
    }

    public String getPayType() {
        return payType;
    }
}
