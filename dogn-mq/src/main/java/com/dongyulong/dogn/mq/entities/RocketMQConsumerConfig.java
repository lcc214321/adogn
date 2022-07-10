package com.dongyulong.dogn.mq.entities;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/97:09 下午
 * @since v1.0
 */
public class RocketMQConsumerConfig implements InitializingBean {
    public static String nameSrvAddr;
    public static int retryThreshold = 20;
    public static int orderlyRetryThreshold = 3;
    public static int retryInterval = 500;
    public static int maxOffsetThreshold = 200000;
    public static String txBeanName;
    public static int consumeTimeoutMins = 3;

    private String _nameSrvAddr;
    private int _orderlyRetryThreshold;
    private int _retryThreshold;
    private int _retryInterval;
    private String _txBeanName;
    private int _maxOffsetThreshold;
    private int _consumeTimeoutMins;


    @Override
    public void afterPropertiesSet() throws Exception {
        nameSrvAddr = _nameSrvAddr;
        if (_orderlyRetryThreshold > 0) {
            orderlyRetryThreshold = _orderlyRetryThreshold;
        }
        if (_retryInterval > 0) {
            retryInterval = _retryInterval;
        }
        if (_retryThreshold > 0) {
            retryThreshold = _retryThreshold;
        }
        if (!StringUtils.isEmpty(_txBeanName)) {
            txBeanName = _txBeanName;
        }
        if (_maxOffsetThreshold > 0) {
            maxOffsetThreshold = _maxOffsetThreshold;
        }
        if (_consumeTimeoutMins > 0) {
            consumeTimeoutMins = _consumeTimeoutMins;
        }
    }

    public void set_nameSrvAddr(String _nameSrvAddr) {
        this._nameSrvAddr = _nameSrvAddr;
    }

    public void set_orderlyRetryThreshold(int _orderlyRetryThreshold) {
        this._orderlyRetryThreshold = _orderlyRetryThreshold;
    }

    public void set_retryThreshold(int _retryThreshold) {
        this._retryThreshold = _retryThreshold;
    }

    public void set_retryInterval(int _retryInterval) {
        this._retryInterval = _retryInterval;
    }

    public void set_txBeanName(String _txBeanName) {
        this._txBeanName = _txBeanName;
    }

    public void set_maxOffsetThreshold(int _maxOffsetThreshold) {
        this._maxOffsetThreshold = _maxOffsetThreshold;
    }

    public void set_consumeTimeoutMins(int _consumeTimeoutMins) {
        this._consumeTimeoutMins = _consumeTimeoutMins;
    }
}
