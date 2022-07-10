package com.dongyulong.dogn.autoconfigure.merchant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjectUtil;
import com.dongyulong.dogn.common.exception.DException;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.autoconfigure.merchant.entities.AbstractConfig;
import com.dongyulong.dogn.autoconfigure.merchant.entities.Config;
import com.dongyulong.dogn.autoconfigure.merchant.entities.PMerchantInfoModel;
import com.dongyulong.dogn.autoconfigure.merchant.mapper.PMerchantInfoMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * pay handler配置加载器
 *
 * @author dongy
 * @date 14:47 2022/3/23
 **/
@Slf4j
public abstract class AbstractMchConfigLoader<T extends AbstractConfig> extends TypeReference<AbstractMchConfigLoader<T>> implements IMchConfigDecoder<T> {

    protected final PMerchantInfoMapper pMerchantInfoMapper;
    protected final String channelType;

    protected AbstractMchConfigLoader(String channelType, PMerchantInfoMapper pMerchantInfoMapper) {
        if (pMerchantInfoMapper == null) {
            throw new DException(ErrorCode.MERCHANT_MAPPER_ERROR);
        }
        this.channelType = channelType;
        this.pMerchantInfoMapper = pMerchantInfoMapper;
        initConfig();
        init(configMap);
    }

    /**
     * 配置组 同一个channel下的所有配置
     * key为{@link Config#getMchUniqueNo()}
     */
    @Getter
    private final Map<String, Config<T>> configMap = new ConcurrentHashMap<>(64);

    public Config<T> getConfig(String mchUniqueNo) {
        return configMap.computeIfAbsent(mchUniqueNo, this::loadOneConfig);
    }

    private void initConfig() {
        List<PMerchantInfoModel> pMerchantInfoModels = pMerchantInfoMapper.selectListByChannel(this.channelType);
        if (CollUtil.isEmpty(pMerchantInfoModels)) {
            log.warn("{}获取渠道商户配置信息异常 channel:{}", this.getClass(), this.channelType);
            return;
        }
        Map<String, Config<T>> initConfigMap = pMerchantInfoModels.stream()
                .collect(Collectors.toMap(PMerchantInfoModel::getMchUniqueNo,
                        pMerchantInfoModel -> this.toConfig(this.targetClass(), pMerchantInfoModel)));
        if (CollUtil.isEmpty(initConfigMap)) {
            return;
        }
        configMap.putAll(initConfigMap);
    }

    protected Config<T> loadOneConfig(String mchUniqueNo) {
        PMerchantInfoModel condition = new PMerchantInfoModel();
        condition.setMchUniqueNo(mchUniqueNo);
        PMerchantInfoModel pMerchantInfoModel = pMerchantInfoMapper.selectOneByCondition(condition);
        if (ObjectUtil.isEmpty(pMerchantInfoModel)) {
            log.error("{}获取渠道商户配置信息异常 mchUniqueNo:{},channel:{}", this.getClass(), mchUniqueNo, this.channelType);
            throw new DException(ErrorCode.CONFIG_INFO_ERROR);
        }
        return this.toConfig(this.targetClass(), pMerchantInfoModel);
    }

    private Class<T> targetClass() {
        return (Class<T>) TypeFactory.rawClass(this.getType());
    }

    /**
     * 初始化实例属性
     *
     * @param configMap 启动后加载到的所有当前渠道的商户配置信息
     */
    protected abstract void init(Map<String, Config<T>> configMap);
}
