package com.dongyulong.dogn.autoconfigure.merchant.entities;

import lombok.Getter;

import java.util.Arrays;

/**
 * 渠道类型,大的渠道类型,不是细分的商户渠道类型
 *
 * @author zhangshaolong
 * @date 2022/1/17
 **/
@Getter
public enum ChannelType {

    /**
     * 微信支付
     */
    WEIXIN(ChannelType.CHANNEL_WEIXIN),
    /**
     * 支付宝
     */
    ALIPAY(ChannelType.CHANNEL_ALIPAY),
    /**
     * 京东
     */
    JING_DONG(ChannelType.CHANNEL_JINGDONG),
    /**
     * 云闪付
     */
    UNION_PAY(ChannelType.CHANNEL_UNIONPAY),
    /**
     * 西安银行
     */
    XIAN_BANK(ChannelType.CHANNEL_XIANBANK),
    /**
     * 嘀嗒白条
     */
    DDBT(ChannelType.CHANNEL_DDBT),
    /**
     * 银盛
     */
    YS_PAY(ChannelType.CHANNEL_YSPAY),
    /**
     * 平安
     */
    PING_AN(ChannelType.CHANNEL_PINGAN),
    /**
     * 系统操作
     **/
    SYSTEM("system");

    private final String name;

    public static final String CHANNEL_WEIXIN = "weixin";
    public static final String CHANNEL_ALIPAY = "alipay";
    public static final String CHANNEL_JINGDONG = "jingdong";
    public static final String CHANNEL_UNIONPAY = "unionpay";
    public static final String CHANNEL_XIANBANK = "xianbank";
    public static final String CHANNEL_DDBT = "ddbt";
    public static final String CHANNEL_YSPAY = "yspay";
    public static final String CHANNEL_PINGAN = "pingan";
    public static final String[] CHANNELS = Arrays.stream(ChannelType.values()).map(ChannelType::getName).toArray(String[]::new);


    ChannelType(String name) {
        this.name = name;
    }


    public String getName() {
        return this.name;
    }

    public static ChannelType findByName(String name) {
        return Arrays.stream(ChannelType.values())
                .filter(typeEnum -> typeEnum.name.equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }

}
