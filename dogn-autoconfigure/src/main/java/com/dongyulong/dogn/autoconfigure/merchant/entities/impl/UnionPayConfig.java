package com.dongyulong.dogn.autoconfigure.merchant.entities.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.dongyulong.dogn.autoconfigure.merchant.annotation.AbleConfig;
import com.dongyulong.dogn.autoconfigure.merchant.entities.AbstractConfig;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author dongy
 * @date 20:39 2022/2/10
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UnionPayConfig extends AbstractConfig {

    public static UnionPayConfig of() {
        return new UnionPayConfig();
    }

    /**
     * 私钥或者私钥证书
     */
    @AbleConfig(file = true)
    private byte[] privateKey;
    /**
     * 验签中级证书路径
     * 可配置的
     */
    @AbleConfig
    private String rootKeyCert;
    /**
     * 验签根证书路径
     */
    @AbleConfig
    private String keyCertPassWord;
    /**
     * 中级证书路径
     */
    @AbleConfig
    private String middleKeyCert;
    /**
     * 是否验证验签证书CN身份，除了false都验
     */
    private boolean ifValidateCnName = true;
    /**
     * 是否验证https证书，默认都不验
     */
    private boolean ifValidateRemoteCert = true;
    /**
     * signMethod，没配按01吧
     */
    private String signMethod = "01";
    /**
     * version，没配按5.1.0
     */
    private String version = "5.1.0";
    /**
     * 签名证书类型
     */
    private String signCertType = "PKCS12";
    /**
     * 编码方式
     */
    private Charset encoding = StandardCharsets.UTF_8;
    /**
     * 接入类型
     * 0：普通商户直连接入
     * 1：收单机构接入
     * 2：平台类商户接入
     */
    private String accessType = "0";
    /**
     * 账号类型
     */
    private String accType = "01";
    /**
     * 交易币种,默认为156
     */
    private String currencyCode = "156";
    /**
     * 渠道类型
     */
    private String channelType = "08";
    /**
     * 请求域模板
     * （营销标识）
     */
    @Getter(AccessLevel.PRIVATE)
    private String reservedTemplate = "{discountCode={}}";

    /**
     * @param discountCode 营销码云闪付提供
     * @return -
     */
    public String genReserved(String discountCode) {
        return CharSequenceUtil.format(reservedTemplate, discountCode);
    }
}
