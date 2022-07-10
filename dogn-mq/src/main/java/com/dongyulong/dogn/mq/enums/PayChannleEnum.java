package com.dongyulong.dogn.mq.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author 王雪源
 * @Date 2021/12/17 13:33
 * @Version 1.0
 *
 * 支付渠道
 *
 */
public enum PayChannleEnum {


    /**
     *  京东
     */
    JINGDONG("jingdong"),
    /**
     * 支付宝
     */
    ALIPAY("alipay"),
    /**
     *  微信
     */
    WEIXIN("weixin"),
    /**
     * 西安银行支付宝
     */
    XABANK_ALI("xabank_ali"),
    /**
     * 西安银行微信
     */
    XACBANK("xacbank"),
    /**
     * 嘀嗒白条
     */
    DIDA_JDBT("dida_jdbt"),
    /**
     * 云闪付
     */
    UNIONPAY("unionpay"),
    /**
     * 广州微信
     */
    GZ_WEIXIN("gz_weixin");

    private String payChannle;

    public String getPayChannle() {
        return payChannle;
    }

    public void setPayChannle(String payChannle) {
        this.payChannle = payChannle;
    }

    PayChannleEnum(String payChannle) {
        this.payChannle = payChannle;
    }

    private static Map<String, PayChannleEnum> payChannleEnumHashMap = new HashMap<>();
    static{
        PayChannleEnum[] values = PayChannleEnum.values();
        for (PayChannleEnum payChannleEnum: values){
            payChannleEnumHashMap.put(payChannleEnum.getPayChannle(),payChannleEnum);
        }
    }

    public static PayChannleEnum getPayChannleEnum(String payChannle){
        return payChannleEnumHashMap.get(payChannle);
    }
}
