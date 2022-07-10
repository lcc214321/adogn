package com.dongyulong.dogn.autoconfigure.merchant;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.dongyulong.dogn.common.exception.DException;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.autoconfigure.merchant.annotation.AbleConfig;
import com.dongyulong.dogn.autoconfigure.merchant.codec.ConfigCodec;
import com.dongyulong.dogn.autoconfigure.merchant.entities.AbstractConfig;
import com.dongyulong.dogn.autoconfigure.merchant.entities.Config;
import com.dongyulong.dogn.autoconfigure.merchant.entities.MerchantConfigDO;
import com.dongyulong.dogn.autoconfigure.merchant.entities.PMerchantInfoModel;
import com.dongyulong.dogn.tools.json.JsonMapper;
import com.dongyulong.dogn.tools.json.JsonTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * pay handler配置解码器
 *
 * @author dongy
 * @date 14:47 2022/3/23
 **/
public interface IMchConfigDecoder<T extends AbstractConfig> {

    Logger log = LoggerFactory.getLogger(IMchConfigDecoder.class);

    /**
     * 解析数据,通过setter方法设置数据
     *
     * @param configTargetClass -
     * @param merchantInfo      -
     * @return -
     */
    default Config<T> doDecode(final Class<T> configTargetClass, MerchantConfigDO merchantInfo) {
        T config;
        try {
            config = configTargetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("{}#doDecode 反射实例化对象失败 configTargetClass:{}merchantInfo:{}", this.getClass(), configTargetClass, JsonMapper.toJson(merchantInfo));
            throw new DException(ErrorCode.CONFIG_INFO_ERROR);
        }
        if (StringUtils.isBlank(merchantInfo.getConfig())) {
            return Config.Builder.build(merchantInfo, config);
        }
        Map<String, Object> configMap = JsonTools.toMap(merchantInfo.getConfig());
        Field[] declaredFields = FieldUtils.getAllFields(configTargetClass);
        for (Field field : declaredFields) {
            this.doFinalDecoder(field, config, configMap.get(field.getName()));
        }
        return Config.Builder.build(merchantInfo, config);
    }

    /**
     * 执行最终解密
     *
     * @param field  -
     * @param config -
     * @param value  -
     */
    default void doFinalDecoder(final Field field, final T config, final Object value) {
        AbleConfig ableConfig = field.getAnnotation(AbleConfig.class);
        if (ableConfig == null || value == null) {
            return;
        }
        final String setterPrefix = "set{}";
        String setterMethodName = CharSequenceUtil.format(setterPrefix, StringUtils.capitalize(field.getName()));
        Method method = MethodUtils.getAccessibleMethod(config.getClass(), setterMethodName, field.getType());
        if (method == null) {
            log.warn(CharSequenceUtil.format("商户参数解密失败 未找到setter方法 setterMethodName:{} ", setterMethodName));
            return;
        }
        Object arg = value;
        if (ableConfig.decrypt()) {
            arg = ConfigCodec.toDecrypt(value, field.getType());
        }
        try {
            method.invoke(config, arg);
        } catch (Exception e) {
            log.error(CharSequenceUtil.format("商户参数解密失败 name:{}", field.getName()), e);
            throw new DException(ErrorCode.CONFIG_INFO_ERROR);
        }
    }


    /**
     * 配置信息解密并返回配置信息 {@link MerchantConfigDO}
     *
     * @param configTargetClass -
     * @param merchantInfo      -
     * @return -
     */
    default Config<T> toConfig(final Class<T> configTargetClass, PMerchantInfoModel merchantInfo) {
        if (merchantInfo == null) {
            return null;
        }
        MerchantConfigDO merchantConfigDO = BeanUtil.copyProperties(merchantInfo, MerchantConfigDO.class);
        return this.doDecode(configTargetClass, merchantConfigDO);
    }

}
