package com.dongyulong.dogn.apollo.autoconfigure;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/94:09 下午
 * @since v1.0
 */
public class ApolloConfigureInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext>, EnvironmentPostProcessor, Ordered {

    private static Logger logger = LoggerFactory.getLogger(ApolloConfigureInitializer.class);

    /**
     * @param applicationContext
     */
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        initializeAppId();
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        initializeAppId();
    }

    public void initializeAppId() {
        Properties prop = yaml2Properties("application.yml");

        Object[] propNames = prop.keySet().toArray();
        for (int i = 0; i < propNames.length; i++) {
            String name = String.valueOf(propNames[i]);
            if (!name.startsWith("spring") && !name.startsWith("server") && !name.startsWith("thrift")) {
                logger.error("检测到非法本地配置，请移除后再试。项目个性化配置请在Apollo系统中添加，系统地址 http://apollo.didapinche.com/index.html");
                System.out.println("检测到非法本地配置，请移除后再试。项目个性化配置请在Apollo系统中添加，系统地址 http://apollo.didapinche.com/index.html");
                System.exit(1);
            }
        }

        String appId = (String) prop.get("spring.application.name");

        if (Strings.isNullOrEmpty(appId)) {
            return;
        }

        System.setProperty("app.id", appId);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public static Properties yaml2Properties(String yamlSource) {
        try {
            YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
            yaml.setResources(new ClassPathResource(yamlSource));
            return yaml.getObject();
        } catch (Exception e) {
            logger.error("Cannot read yaml", e);
            return null;
        }
    }
}
