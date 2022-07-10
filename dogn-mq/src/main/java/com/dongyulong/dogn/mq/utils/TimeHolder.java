package com.dongyulong.dogn.mq.utils;

import java.util.concurrent.TimeUnit;

/**
 * 统计时间信息
 * @author zhangshaolong
 * @create 2022/1/18
 * */
public class TimeHolder {

    private static ThreadLocal<Long> holder = new ThreadLocal<>();

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

    public static void start() {
        holder.set(currentTimeMillis());
    }

    public static Long stop() {
        return currentTimeMillis()-holder.get();
    }

    public static long currentTimeMillis() {
        return currentTimeMillis;
    }

}
