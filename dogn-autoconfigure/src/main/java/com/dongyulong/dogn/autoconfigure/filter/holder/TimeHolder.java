package com.dongyulong.dogn.autoconfigure.filter.holder;

/**
 * 统计时间信息
 * @author zhangshaolong
 * @create 2021/12/17
 * */
public class TimeHolder {

    private static ThreadLocal<Long> holder = new ThreadLocal<>();

    public static void put(Long time) {
        holder.set(time);
    }

    public static Long get() {
        return holder.get() == null ? System.currentTimeMillis() : holder.get();
    }

    public static void remove() {
        holder.remove();
    }
}
