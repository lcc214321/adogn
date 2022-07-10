package com.dongyulong.dogn.core.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 日志打印
 *
 * @author zhangshaolong
 * @create 2022/2/14
 **/
public class LogHelper {

    private static final Logger SLOW = LoggerFactory.getLogger("SlowLogger");

    public static void slowLog(String message) {
        SLOW.info(message);
    }

    public static void logSlow(long start, long peak, String method) {
        long end = System.currentTimeMillis();
        if (end > start + peak) {
            slowLog(String.format("%s cost:%sms", method, end - start));
        }
    }

}
