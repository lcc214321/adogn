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
public class XiAnBankConfig extends AbstractConfig {

    public static XiAnBankConfig of() {
        return new XiAnBankConfig();
    }

    private static final String XIAN_BANK_URL = "https://c.xacbank.com:8000/api/OpenApp";
    /**
     * 私钥或者私钥证书
     */
    @AbleConfig
    private String privateKey;
    /**
     * useScenario
     */
    @AbleConfig(decrypt = false)
    private String useScenario;
    /**
     * userAppId
     */
    @AbleConfig(decrypt = false)
    private String userAppId;
    /**
     * activityId
     */
    @AbleConfig(decrypt = false)
    private String activityId;
    /**
     * appSecret
     */
    @AbleConfig
    private String appSecret;

}
