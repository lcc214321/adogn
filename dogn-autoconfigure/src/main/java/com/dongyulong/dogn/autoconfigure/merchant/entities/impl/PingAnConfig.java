package com.dongyulong.dogn.autoconfigure.merchant.entities.impl;

import com.dongyulong.dogn.autoconfigure.merchant.annotation.AbleConfig;
import com.dongyulong.dogn.autoconfigure.merchant.entities.AbstractConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * pay-authentication
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/5/138:23 下午
 * @since v1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PingAnConfig extends AbstractConfig {
    /**
     * 平安开放平台应用ID
     */
    @AbleConfig(decrypt = false)
    private String clientId;
    /**
     * 平安开放平台应用密钥
     */
    @AbleConfig(decrypt = false)
    private String clientSecret;
    /**
     * 报文ASE对称加密/解密密码
     */
    @AbleConfig(decrypt = false)
    private String password;

}
