package com.dongyulong.dogn.autoconfigure.merchant.entities;

import cn.hutool.core.annotation.PropIgnore;
import cn.hutool.core.bean.BeanUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dongy
 * @date 19:00 2022/2/14
 **/
@Slf4j
@Data
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config<T extends AbstractConfig> {

    @SneakyThrows
    private Config(MerchantConfigDO merchantInfo, T config) {
        BeanUtil.copyProperties(merchantInfo, this);
        this.config = config;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        public static <T extends AbstractConfig> Config<T> build(MerchantConfigDO merchantInfo,
                                                                 T config) {
            return new Config<>(merchantInfo, config);
        }
    }


    /**
     * 渠道类型
     */
    private String channelType;
    /**
     * 三方支付渠道分配的商户号，如果没有可根据需要设置为app_id或id
     */
    private String mchNo;
    /**
     * 父级商户号
     */
    private String preMchNo;
    /**
     * 商户唯一编号，跟channel_type+mch_no唯一对应
     */
    private String mchUniqueNo;
    /**
     * 应用id,如果存在同一套配置信息多个app_id,需要关联表中定义出app_id
     */
    private String appId;
    /**
     * 商户全称
     */
    private String mchName;
    /**
     * 配置信息
     */
    @PropIgnore
    private T config;

}
