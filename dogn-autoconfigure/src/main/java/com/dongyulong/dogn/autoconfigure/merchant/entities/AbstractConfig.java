package com.dongyulong.dogn.autoconfigure.merchant.entities;

import com.dongyulong.dogn.autoconfigure.merchant.annotation.AbleConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据{@link PMerchantInfoModel#getConfig()}的明文数据
 * 皆由当前类型的实现类转成json后加密生成
 * 属性标记为{@link AbleConfig}表示允许配置,未被标记的不允许配置
 *
 * @author dongy
 * @date 20:50 2022/2/10
 **/
@Slf4j
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public abstract class AbstractConfig {

    /**
     * 默认的支付超时时间,单位毫秒、ms
     * 默认10分钟
     */
    private Integer defaultPayTimeOut = 600000;


}
