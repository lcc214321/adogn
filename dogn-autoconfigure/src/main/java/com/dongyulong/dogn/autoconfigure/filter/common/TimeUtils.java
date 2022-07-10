package com.dongyulong.dogn.autoconfigure.filter.common;

import java.util.concurrent.TimeUnit;

/**
 * 时间的工具包
 * @author zhang.shaolong
 * @create 2021/12/17
 **/
public class TimeUtils {
    private static volatile long currentTimeMillis;
    static {
        currentTimeMillis = System.currentTimeMillis();
        Thread daemon = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    currentTimeMillis = System.currentTimeMillis();
                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (Throwable e) {

                    }
                }
            }
        });
        daemon.setDaemon(true);
        daemon.setName("time-tick-thread");
        daemon.start();
    }

    public static long currentTimeMillis() {
        return currentTimeMillis;
    }

    public static long timeCost(long startTime) {
        return currentTimeMillis-startTime;
    }
}
