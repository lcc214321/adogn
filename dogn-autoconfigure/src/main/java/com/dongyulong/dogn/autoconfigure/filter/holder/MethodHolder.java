package com.dongyulong.dogn.autoconfigure.filter.holder;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * @author zhangshaolong
 * @create 2021/12/20
 **/
public class MethodHolder {

    private static FastThreadLocal<String> holder = new FastThreadLocal<>();

    public static void put(String method) {
        holder.set(method);
    }

    public static String get() {
        return holder.get();
    }

    public static void remove() {
        holder.remove();
    }
}
