package com.dongyulong.dogn.autoconfigure.merchant.entities.impl;

import com.dongyulong.dogn.autoconfigure.merchant.annotation.AbleConfig;
import com.dongyulong.dogn.autoconfigure.merchant.entities.AbstractConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dongy
 * @date 20:39 2022/2/10
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class JingDongConfig extends AbstractConfig {

    public static JingDongConfig of() {
        return new JingDongConfig();
    }

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
     * desKey
     */
    @AbleConfig
    private String desKey;
    /**
     * md5Key
     */
    @AbleConfig
    private String md5Key;
    /**
     * version
     */
    private String version = "V2.0";
    /**
     * form
     */
    private String form = "application/xml";
    /**
     * 商户订单的标题/商品名称/关键字等
     */
    private String tradeName = "嘀嗒出行";
    /**
     * 商户订单的具体描述信息
     */
    private String tradeDesc = "嘀嗒出行";
    /**
     * 货币类型。固定值：CNY
     */
    private String currency = "CNY";
    /**
     * 0-实物，1-虚拟
     */
    private String orderType = "1";
    /**
     * 交易过期时间,单位：秒
     */
    private String expireTime = "600";
    /**
     * 收款商户
     * 收银台展示的收款商户，默认为商户号对应的商户
     */
    private String payMerchant = "嘀嗒出行";
    /**
     * 收货人姓名
     */
    private String name = "嘀嗒出行";
    /**
     * 收货地址
     */
    private String address = "北京市朝阳区创远路36号院朝来科技园";
    /**
     * 收货手机号
     */
    private String phone = "400-163-0886";
    private String goodsSubmittedCustoms = "N";
    private String goodsUnderBonded = "N";
    /**
     * 业务通道类型
     */
    private String bizTp = "100007";

}
