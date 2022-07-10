package com.dongyulong.dogn.tools.system;

import com.dongyulong.dogn.tools.json.JsonMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/88:41 上午
 * @since v1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SystemPropertyUtils {

    public static Integer getIntProperty(String key, int def) {
        String propertyVal = System.getProperty(key);
        if (StringUtils.isBlank(propertyVal)) {
            return def;
        }
        if (!StringUtils.isNumeric(propertyVal)) {
            return def;
        }
        return Integer.parseInt(propertyVal);
    }

    public static Long getLongProperty(String key, long def) {
        String propertyVal = System.getProperty(key);
        if (StringUtils.isBlank(propertyVal)) {
            return def;
        }
        if (!StringUtils.isNumeric(propertyVal)) {
            return def;
        }
        return Long.parseLong(propertyVal);
    }

    public static <T> T getProperty(String key, Class<T> clazz, T def) {
        String propertyVal = System.getProperty(key);
        if (StringUtils.isBlank(propertyVal)) {
            return def;
        }
        if (!StringUtils.isNumeric(propertyVal)) {
            return def;
        }
        return JsonMapper.json2Bean(propertyVal, clazz);
    }

    public static String getProperty(String key, String def) {
        return System.getProperty(key, def);
    }

    public static String getProperty(String key) {
        return System.getProperty(key);
    }
}
