package com.dongyulong.dogn.common.config;

import com.dongyulong.dogn.tools.system.SystemPropertyUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/84:31 下午
 * @since v1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUtils {
    private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    private static String host_name = null;
    private static String app_name = null;

    public static List<String> getAllKeys(Enumeration<String> now, Enumeration<String> past) {
        List<String> keys = new ArrayList<>();

        while (now.hasMoreElements()) {
            keys.add(now.nextElement());
        }

        while (past.hasMoreElements()) {
            String key = past.nextElement();
            if (!keys.contains(key)) {
                keys.add(key);
            }
        }

        return keys;
    }

    public static String getHostName() {
        if (host_name == null) {
            try {
                host_name = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                logger.error("unknow host", e);
            }
        }
        return host_name;
    }

    public static String getAppName() {
        if (app_name == null) {
            app_name = SystemPropertyUtils.getProperty("spring.application.name");
        }
        return app_name;
    }
}
