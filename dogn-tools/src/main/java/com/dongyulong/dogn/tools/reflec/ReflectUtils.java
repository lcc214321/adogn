package com.dongyulong.dogn.tools.reflec;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/97:43 上午
 * @since v1.0
 */
@Slf4j
public class ReflectUtils {

    private static final String SETTER_PREFIX = "set";
    private static final String GETTER_PREFIX = "get";

    public static void invokeSetter(Object obj, String propertyName, Object... args) {
        String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(propertyName);
        ReflectUtil.invoke(obj, setterMethodName, args);
    }

    public static Object invokeGetter(Object obj, String propertyName) {
        String setterMethodName = GETTER_PREFIX + StringUtils.capitalize(propertyName);
        return ReflectUtil.invoke(obj, setterMethodName);
    }
}
