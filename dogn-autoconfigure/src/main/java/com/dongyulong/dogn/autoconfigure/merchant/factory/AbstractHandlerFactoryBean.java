package com.dongyulong.dogn.autoconfigure.merchant.factory;

import cn.hutool.core.lang.TypeReference;
import com.dongyulong.dogn.common.exception.DException;
import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.autoconfigure.merchant.AbstractMchConfigLoader;
import com.dongyulong.dogn.autoconfigure.merchant.annotation.Channel;
import com.dongyulong.dogn.autoconfigure.merchant.entities.AbstractConfig;
import com.dongyulong.dogn.autoconfigure.merchant.mapper.PMerchantInfoMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

import javax.annotation.Resource;

/**
 * @author dongy
 * @date 18:08 2022/3/29
 **/
@Slf4j
public abstract class AbstractHandlerFactoryBean<H extends AbstractMchConfigLoader<? extends AbstractConfig>> extends TypeReference<AbstractHandlerFactoryBean<H>> implements FactoryBean<H> {

    @Getter(lazy = true)
    private final H handler = this.doGetHandler();

    @Resource
    protected PMerchantInfoMapper pMerchantInfoMapper;

    protected final Channel channel() {
        Channel channel = this.getClass().getAnnotation(Channel.class);
        if (channel == null) {
            log.error("{}当前类实现必须使用注解{}标注", this.getClass(), Channel.class);
            throw new DException(ErrorCode.CONFIG_INFO_ERROR);
        }
        return channel;
    }

    /**
     * 加载handler
     *
     * @return -
     */
    protected abstract H doGetHandler();

    protected String getChannelType() {
        return channel().channel().getName();
    }

    @Override
    public H getObject() {
        return getHandler();
    }

    @Override
    public Class<?> getObjectType() {
        return TypeFactory.rawClass(getType());
    }

    @Override
    public boolean isSingleton() {
        return this.channel().isSingleton();
    }
}
