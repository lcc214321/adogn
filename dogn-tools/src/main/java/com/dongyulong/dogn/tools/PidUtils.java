package com.dongyulong.dogn.tools;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/912:46 上午
 * @since v1.0
 */
public class PidUtils {

    public static final int getPid() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0]);
    }
}
