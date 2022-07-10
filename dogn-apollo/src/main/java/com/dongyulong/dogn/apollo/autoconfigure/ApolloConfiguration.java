package com.dongyulong.dogn.apollo.autoconfigure;

import com.dongyulong.dogn.apollo.tools.PropertyUtils;
import org.springframework.context.annotation.Bean;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/94:10 下午
 * @since v1.0
 */
public class ApolloConfiguration {

    @Bean
    public ApolloPropertiesRefresher apolloPropertiesRefresher() {
        return new ApolloPropertiesRefresher();
    }

    @Bean
    public PropertyUtils propertyUtils() {
        return new PropertyUtils();
    }

}
