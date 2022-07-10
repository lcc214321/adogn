package com.dongyulong.dogn.apollo.listener;

import com.ctrip.framework.apollo.model.ConfigChange;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/93:59 下午
 * @since v1.0
 */
public interface ApolloChangeListener {

    String getConfigName();

    void update(ConfigChange change);
}
