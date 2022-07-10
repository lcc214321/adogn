package com.dongyulong.dogn.autoconfigure.merchant.entities.impl;

import com.dongyulong.dogn.autoconfigure.merchant.annotation.AbleConfig;
import com.dongyulong.dogn.autoconfigure.merchant.entities.AbstractConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 使用微信sdk微信平台证书
 *
 * @author dongy
 * @date 20:39 2022/2/10
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class WexinConfig extends AbstractConfig {

    public static WexinConfig of() {
        return new WexinConfig();
    }

    /**
     * 私钥或者私钥证书
     */
    @AbleConfig
    private byte[] privateKey;
    /**
     * 支付分参数
     */
    @AbleConfig(decrypt = false)
    private String serviceId;
    /**
     * 商户证书序列号
     */
    @AbleConfig(decrypt = false)
    private String serialNo;
    /**
     * apiV3key
     */
    @AbleConfig
    private byte[] apiV3key;
    /**
     * postPaymentName
     */
    private String postPaymentName = "车费支付";
    /**
     * 品牌主商户号
     */
    @AbleConfig(decrypt = false)
    private String brandMchId = "1617556410";
    /**
     * 品牌主商户appId
     */
    @AbleConfig(decrypt = false)
    private String brandMchAppId = "wx8b1a8c088b38d63b";


}
