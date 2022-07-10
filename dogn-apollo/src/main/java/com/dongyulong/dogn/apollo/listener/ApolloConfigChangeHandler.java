package com.dongyulong.dogn.apollo.listener;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.dongyulong.dogn.apollo.autoconfigure.ApolloConfigureInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/94:08 下午
 * @since v1.0
 */
public class ApolloConfigChangeHandler implements EnvironmentAware {

    private static Logger logger = LoggerFactory.getLogger(ApolloConfigureInitializer.class);
    private static List<Config> configs = new ArrayList<>();
    private static ConcurrentHashMap<String, List<ApolloChangeListener>> listenerMap = new ConcurrentHashMap<>();

    public static void initListener(Environment environment) {
        String namespaces = environment.getProperty("apollo.bootstrap.namespaces");
        if(StringUtils.isEmpty(namespaces)) {
            return;
        }
        for(String namespace : namespaces.split(",")) {
            Config config = ConfigService.getConfig(namespace);
            config.addChangeListener(new ConfigChangeListener() {
                @Override
                public void onChange(ConfigChangeEvent changeEvent) {
                    for (String key : changeEvent.changedKeys()) {
                        ConfigChange change = changeEvent.getChange(key);
                        logger.info(String.format(
                                "Found change - key: %s, oldValue: %s, newValue: %s, changeType: %s",
                                change.getPropertyName(), change.getOldValue(),
                                change.getNewValue(), change.getChangeType()));
                        if(listenerMap.get(change.getPropertyName()) == null) {
                            continue;
                        }
                        for (ApolloChangeListener listener : listenerMap.get(change.getPropertyName())) {
                            listener.update(change);
                        }
                    }
                }
            });
            configs.add(config);
        }
    }

    public synchronized static void register(ApolloChangeListener listener) {
        String configName = listener.getConfigName();
        if(listenerMap.get(configName) == null) {
            listenerMap.put(configName, new ArrayList<ApolloChangeListener>());
        }
        listenerMap.get(configName).add(listener);
    }

    @Override
    public void setEnvironment(Environment environment) {
        initListener(environment);
    }
}
