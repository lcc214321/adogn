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
public class AlipayConfig extends AbstractConfig {

    public static AlipayConfig of() {
        return new AlipayConfig();
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
     * 免密支付销售产品码，商家和支付宝签约的产品码
     */
    private String freePayProductCode = "GENERAL_WITHHOLDING";
    /**
     * 支付销售产品码，商家和支付宝签约的产品码
     */
    private String payProductCode = "QUICK_MSECURITY_PAY";
}
