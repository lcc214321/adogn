package com.dongyulong.dogn.autoconfigure.merchant;

import cn.hutool.core.collection.CollUtil;
import com.dongyulong.dogn.common.exception.DException;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.core.log.LoggerBuilder;
import com.dongyulong.dogn.autoconfigure.merchant.entities.AbstractConfig;
import com.dongyulong.dogn.autoconfigure.merchant.entities.Config;
import com.dongyulong.dogn.autoconfigure.merchant.mapper.PMerchantInfoMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * pay-service
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/5/125:36 下午
 * @since v1.0
 */
public abstract class AbstractFeignClientHandler<T extends AbstractConfig, F> extends AbstractMchConfigLoader<T> {

    protected static final Logger LOG_ACCESS = LoggerBuilder.getLogger("access");

    /**
     * feign client map,for key is mchUniqueNo
     */
    private Map<String, F> feignClients;


    protected AbstractFeignClientHandler(String channelType, ObjectProvider<PMerchantInfoMapper> pMerchantInfoMapperObjectProvider) {
        super(channelType, pMerchantInfoMapperObjectProvider.getIfAvailable());
    }

    @Override
    protected final void init(Map<String, Config<T>> configMap) {
        feignClients = new ConcurrentHashMap<>(64);
        if (CollUtil.isEmpty(configMap)) {
            return;
        }
        configMap.forEach((mchUniqueNo, config) -> feignClients.put(mchUniqueNo, buildFeignClient(config)));
    }

    /**
     * 加载feign client
     *
     * @param configMap 商户配置信息
     * @return -
     */
    protected abstract F buildFeignClient(Config<T> configMap);

    protected F getFeignClient(String mchUniqueNo) {
        Config<T> config = getConfig(mchUniqueNo);
        if (config == null) {
            LOG_ACCESS.error("{}.getFeignClient fail 当前商户号【{}】信息未配置", this.getClass(), mchUniqueNo);
            throw new DException(ErrorCode.UNSUPPORTED_MERCHANT_ID_ERR);
        }
        F feignClient = feignClients.computeIfAbsent(mchUniqueNo, key -> buildFeignClient(config));
        if (feignClient == null) {
            LOG_ACCESS.error("{}.getFeignClient fail 当前商户号【{}】生成商户feign client 失败", this.getClass(), mchUniqueNo);
            throw new DException(ErrorCode.UNSUPPORTED_MERCHANT_ID_ERR);
        }
        return feignClient;
    }
}
