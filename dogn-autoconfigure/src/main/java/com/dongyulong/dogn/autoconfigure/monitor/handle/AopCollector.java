package com.dongyulong.dogn.autoconfigure.monitor.handle;

import org.aspectj.lang.JoinPoint;

/**
 * @author zhangshaolong
 * @create 2021/12/21
 **/
public interface AopCollector {

    /**
     * 方法初始化之前
     * @param jp
     */
    void before(JoinPoint jp,String key);


    /**
     * 正常的业务逻辑处理
     * @param jp
     */
    default void after(Object object,JoinPoint jp,String key,Throwable throwable) {
    }


    /**
     * 其他的业务处理
     * @param jp
     */
    default void afterAop(Object object, JoinPoint jp, String key,long  time) {
    };
}
