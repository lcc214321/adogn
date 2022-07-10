package com.dongyulong.dogn.autoconfigure.merchant.entities.impl;

import com.dongyulong.dogn.autoconfigure.merchant.annotation.AbleConfig;
import com.dongyulong.dogn.autoconfigure.merchant.entities.AbstractConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author dongy
 * @date 20:39 2022/2/10
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class DdbtConfig extends AbstractConfig {
    public static DdbtConfig of() {
        return new DdbtConfig();
    }

    private static final String DDBT_HOST = "http://btgateway.jd.com";
    /**
     * 私钥或者私钥证书
     */
    @AbleConfig
    private String privateKey;
    /**
     * 公钥或者公钥证书
     */
    @AbleConfig
    private String publicKey;

    /**
     * 设备指纹
     */
    @AbleConfig(decrypt = false)
    private String token;
    /**
     * 产线协议key(免密签约需要)
     * 默认:dida_empower_ag_nos
     */
    @AbleConfig(decrypt = false)
    private String agreementKey;
    /**
     * 网关商户号
     */
    @AbleConfig(decrypt = false)
    private String merchantCode;
    /**
     * 渠道值
     */
    private String channelName = "KFPT00100001";
    /**
     * 业务编码
     */
    private String bizCode = "DIDA";
    /**
     * 支付来源
     */
    private String paySource = "DIDA";
    /**
     * 设备指纹
     */
    private String biZid = "DD-BT-LMK";
    /**
     * 商户提供的订单的标题/商品名称/关键字等
     */
    private String tradeName = "嘀嗒出行";
    /**
     * 支付设备标记
     * <p>
     * PC 01 电脑
     * MOBILE 02 手机
     * PAD 03 平板设备
     * WEARABLE 04 可穿戴设备
     * DIGITAL_TV 05 数字电视
     * BAR_CODE 06 条码支付受理终端
     * OTHER 99 其他
     * </p>
     */
    private String payDeviceTag = "02";
    /**
     * 订单类型,0-实物，1-虚拟.固定传1
     */
    private String orderType = "1";
    /**
     * 分期数,本次交易选择的分期期数，不分期则传1.固定传1
     */
    private String planNum = "1";
    /**
     * 订单有效期单位
     * <p>
     * DAYS("天", "DAYS"),
     * HOURS("小时", "HOURS"),
     * MINUTES("分", "MINUTES"),
     * SECONDS("秒", "SECONDS");
     * </p>
     */
    private String expiryUnit = "SECONDS";
    /**
     * 支付场景，固定传值：WDPAY
     */
    private String payScene = "WDPAY";
    /**
     * 嘀嗒白条京东网关类型配置信息
     */
    private DdbtGateWayType gateWayType = DdbtGateWayType.defaultGateWayType();


    /**
     * thirdpay-common
     * 嘀嗒白条京东网关类型配置信息
     *
     * @author dongyulong
     * @version v1.0
     * @date 2021/9/11:38 下午
     * @since v1.0
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class DdbtGateWayType {
        /**
         * 普调支付网关类型
         */
        private String trade = "10061";
        /**
         * 退款网关映射值
         */
        private String refund = "10062";
        /**
         * 交易查询网关
         */
        private String queryTrade = "5008";
        /**
         * 嘀嗒白条发券接口 测试 10175 生产 10124
         */
        private String sendReward = "10124";
        /**
         * 关联验证
         */
        private String correlatingValidation = "2017";
        /**
         * 获取协议接口
         */
        private String acquisitionAgreements = "2011";
        /**
         * 短信鉴权申请
         */
        private String smsApplication = "2013";
        /**
         * 短信鉴权验证
         */
        private String smsAuthorization = "2014";
        /**
         * 授信申请
         */
        private String authApplication = "2015";
        /**
         * 查询开户结果
         */
        private String accountOpeningResult = "2016";
        /**
         * 人脸比对
         */
        private String faceRecognition = "2021";
        /**
         * 账户查询
         */
        private String userAccount = "9611";
        /**
         * 确认关联接口
         */
        private String confirmAssociated = "10118";
        /**
         * 分期试算 测试 10100 生产 10097
         */
        private String trialByInstallments = "10097";
        /**
         * #查询交易结果
         */
        private String postPay = "5008";
        /**
         * 统一登录接口 测试 10030 生产 10080
         */
        private String login = "10080";
        /**
         * 金条查询接口
         */
        private String jt = "40029";
        /**
         * 优惠券查询接口 测试 10171  生产 10119
         */
        private String tickCoupon = "10119";
        /**
         * 新京东查询接口 测试 10055 生产 10126
         */
        private String queryCouponList = "10126";

        /**
         * 生成嘀嗒白条京东网关类型配置信息
         *
         * @return {@link DdbtGateWayType}
         */
        public static DdbtGateWayType defaultGateWayType() {
            return new DdbtGateWayType();
        }
    }
}
