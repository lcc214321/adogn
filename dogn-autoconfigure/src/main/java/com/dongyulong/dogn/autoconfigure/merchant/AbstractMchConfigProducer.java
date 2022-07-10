package com.dongyulong.dogn.autoconfigure.merchant;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.text.CharSequenceUtil;
import com.dongyulong.dogn.common.exception.DException;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.common.exception.IgnoreException;
import com.dongyulong.dogn.autoconfigure.merchant.annotation.AbleConfig;
import com.dongyulong.dogn.autoconfigure.merchant.codec.ConfigCodec;
import com.dongyulong.dogn.autoconfigure.merchant.entities.AbstractConfig;
import com.dongyulong.dogn.autoconfigure.merchant.entities.Config;
import com.dongyulong.dogn.autoconfigure.merchant.entities.MerchantConfigDO;
import com.dongyulong.dogn.autoconfigure.merchant.entities.PMerchantInfoModel;
import com.dongyulong.dogn.tools.json.JsonTools;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.ToIntFunction;

/**
 * 商户信息配置编码器
 *
 * @author dongy
 * @date 14:47 2022/3/23
 **/
public abstract class AbstractMchConfigProducer<T extends AbstractConfig> extends TypeReference<AbstractMchConfigProducer<T>> {

    private static final Logger log = LoggerFactory.getLogger(AbstractMchConfigProducer.class);

    @Getter(lazy = true)
    private final Class<T> targetConfigClass = (Class<T>) TypeFactory.rawClass(getType());

    /**
     * 编码数据
     *
     * @param merchantInfo -
     * @return -
     */
    protected final Config<T> doEncode(MerchantConfigDO merchantInfo) {
        T config;
        try {
            config = this.getTargetConfigClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("{}#doDecode 反射实例化对象失败 configTargetClass:{}merchantInfo:{}", this.getClass(), this.getTargetConfigClass(), JsonTools.toJSON(merchantInfo));
            throw new DException(ErrorCode.CONFIG_INFO_ERROR);
        }
        if (StringUtils.isBlank(merchantInfo.getConfig())) {
            return Config.Builder.build(merchantInfo, config);
        }
        Map<String, Object> configMap = JsonTools.toMap(merchantInfo.getConfig());
        Field[] declaredFields = FieldUtils.getFieldsWithAnnotation(this.getTargetConfigClass(), AbleConfig.class);
        for (Field field : declaredFields) {
            if (!configMap.containsKey(field.getName()) || configMap.get(field.getName()) == null) {
                continue;
            }
            this.doFinalEncode(field, config, configMap.get(field.getName()));
        }
        return Config.Builder.build(merchantInfo, config);
    }

    /**
     * 执行最终加密
     *
     * @param field  -
     * @param config -
     * @param value  -
     */
    protected final void doFinalEncode(final Field field, final T config, final Object value) {
        AbleConfig ableConfig = field.getAnnotation(AbleConfig.class);
        if (ableConfig == null) {
            return;
        }
        final String setterPrefix = "set{}";
        String setterMethodName = CharSequenceUtil.format(setterPrefix, StringUtils.capitalize(field.getName()));
        Method method = MethodUtils.getAccessibleMethod(config.getClass(), setterMethodName, field.getType());
        if (method == null) {
            log.warn(CharSequenceUtil.format("商户参数加密失败 未找到setter方法 setterMethodName:{} ", setterMethodName));
            return;
        }
        Object arg = value;
        //如果当前属性标记为文件,则为文件http链接地址
        if (ableConfig.decrypt()) {
            arg = ConfigCodec.toEncrypt(value.toString(), field.getType(), ableConfig.file());
        }
        try {
            method.invoke(config, arg);
        } catch (Exception e) {
            log.error(CharSequenceUtil.format("商户参数加密失败 name:{}", field.getName()), e);
            throw new DException(ErrorCode.CONFIG_INFO_ERROR);
        }
    }


    /**
     * 新增配置信息
     *
     * @param merchantInfo -
     * @param function     -
     */
    public void addMchInfo(MerchantConfigDO merchantInfo, ToIntFunction<PMerchantInfoModel> function) {
        Config<T> config = this.doEncode(merchantInfo);
        PMerchantInfoModel pMerchantInfoModel = new PMerchantInfoModel();
        BeanUtil.copyProperties(config, pMerchantInfoModel);
        pMerchantInfoModel.setConfig(this.toJson(config.getConfig()));
        pMerchantInfoModel.setCreateTime(new Date());
        pMerchantInfoModel.setUpdateTime(new Date());
        pMerchantInfoModel.setOwner("999");
        int insertCol = function.applyAsInt(pMerchantInfoModel);
        if (insertCol < 1) {
            throw new IgnoreException(ErrorCode.CONFIG_INFO_INSERT_ERROR);
        }
    }

    public String toJson(T config) {
        Field[] declaredFields = FieldUtils.getFieldsWithAnnotation(this.getTargetConfigClass(), AbleConfig.class);
        Map<String, Object> map = new HashMap<>(declaredFields.length * 2);
        final String getterPrefix = "get{}";
        for (Field field : declaredFields) {
            AbleConfig ableConfig = field.getAnnotation(AbleConfig.class);
            if (ableConfig == null) {
                continue;
            }
            String getterMethodName = CharSequenceUtil.format(getterPrefix, StringUtils.capitalize(field.getName()));
            Method method = MethodUtils.getAccessibleMethod(this.getTargetConfigClass(), getterMethodName);
            if (method == null) {
                log.warn(CharSequenceUtil.format("商户参数加密失败 未找到getter方法 getterMethodName:{} ", getterMethodName));
                continue;
            }
            Object value;
            try {
                value = method.invoke(config);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error(CharSequenceUtil.format("商户参数加密失败 name:{}", field.getName()), e);
                throw new DException(ErrorCode.CONFIG_INFO_ERROR);
            }
            if (byte[].class.equals(field.getType())) {
                value = org.apache.commons.codec.binary.StringUtils.newStringUtf8((byte[]) value);
            }
            map.put(field.getName(), value);
        }
        return JsonTools.toJSON(map);
    }

}
