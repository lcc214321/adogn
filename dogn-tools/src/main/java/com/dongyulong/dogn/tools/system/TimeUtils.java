package com.dongyulong.dogn.tools.system;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/89:18 上午
 * @since v1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeUtils {

    /**
     * 获取系统时间戳
     *
     * @return 毫秒
     */
    public static long toSystemSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }
}
