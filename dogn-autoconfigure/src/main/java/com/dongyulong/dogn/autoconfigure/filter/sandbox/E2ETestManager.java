package com.dongyulong.dogn.autoconfigure.filter.sandbox;

import com.alibaba.ttl.TransmittableThreadLocal;

public class E2ETestManager {

    public static final TransmittableThreadLocal<Boolean> ttl = new TransmittableThreadLocal();

    public static void reset(){
        ttl.remove();
    }

    public static void start(){
        ttl.set(true);
    }

    public static boolean isE2ETest(){
        if(ttl.get() == null){
            return false;
        } else {
            return true;
        }
    }
}
