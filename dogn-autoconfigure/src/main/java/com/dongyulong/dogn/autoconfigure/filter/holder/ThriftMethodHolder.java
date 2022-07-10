package com.dongyulong.dogn.autoconfigure.filter.holder;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * thrift的方法名信息
 * @author zhangshaolong
 * @create 2022/1/21
 **/
public class ThriftMethodHolder {

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
