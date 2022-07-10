package com.dongyulong.dogn.mq.core;

/**
 * 异常逻辑处理
 * @author zhangshaolong
 * @create 2021/12/30
 **/
public interface ResultCallback {

     /**
      * 获取返回结果信息
      * @param result
      */
     void callBack(boolean result);
}
