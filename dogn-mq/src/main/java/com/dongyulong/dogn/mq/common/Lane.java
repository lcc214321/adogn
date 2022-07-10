package com.dongyulong.dogn.mq.common;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.StringUtils;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/97:24 下午
 * @since v1.0
 */
public class Lane {

    private static final String DEFAULT_LANE = "DEFAULT";
    public static final String HEADER_NAME = "Lane";
    public static final String ENV_NAME = "LANE";
    private static final ThreadLocal<String> LANE = new TransmittableThreadLocal<>();

    public Lane() {
    }

    public static void set(String str) {
        if (str != null && !str.isEmpty()) {
            LANE.set(str);
        }
    }

    public static String getLane() {
        String lane = Lane.get();
        if (StringUtils.isNotEmpty(lane)) {
            return lane;
        }
        String sysLane = System.getenv(Lane.ENV_NAME);
        if (StringUtils.isNotEmpty(sysLane)) {
            return sysLane;
        }
        return DEFAULT_LANE;
    }

    public static String get() {
        return (String) LANE.get();
    }

    public static void clear() {
        LANE.remove();
    }
}
