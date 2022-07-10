package com.dongyulong.dogn.apollo.autoconfigure;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.dongyulong.dogn.apollo.tools.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScope;

/**
 * Apollo 通过@ConfigurationProperties 注入的配置无法热更新,
 * 通过该类实现这种配置的热更新
 * 注： 只能更新 application 命名空间
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/94:37 下午
 * @since v1.0
 */
public class ApolloPropertiesRefresher {

    private static final Logger logger = LoggerFactory.getLogger(ApolloPropertiesRefresher.class);

    @Autowired
    private RefreshScope refreshScope;

    @ApolloConfigChangeListener
    public void onChange(ConfigChangeEvent changeEvent) {
        for (String key : changeEvent.changedKeys()) {
            logger.info("apollo config changed key:{} oldValue:{} newValue:{}", key, changeEvent.getChange(key).getOldValue(), changeEvent.getChange(key).getNewValue());
        }
        if (PropertyUtils.getProperty("refreshAllEnableAfterApolloConfigChanged", Boolean.class, false)) {
            refreshScope.refreshAll();
            logger.info("apollo config changed and refreshed all bean.");
        }
    }
}
