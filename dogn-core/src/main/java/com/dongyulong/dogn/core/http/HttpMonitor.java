package com.dongyulong.dogn.core.http;

import com.dongyulong.dogn.core.monitor.Counter;

/**
 * @author zhangshaolong
 * @create 2022/1/26
 **/
public class HttpMonitor {

    /**
     * 打点的数据信息
     */
    public Counter failCounter = new Counter();
    public Counter code4xx = new Counter();
    public Counter code5xx = new Counter();
    public Counter qpsCounter = new Counter();

    /**
     * 统计打点数据
     * @param success
     * @param executionTime
     */
    public void record(boolean success, int code,long executionTime) {
        qpsCounter.inc();
        qpsCounter.incrTime(executionTime);
        if (!success || code != 200) {
            failCounter.inc();
        }
        if (code < 500 && code >= 400) {
            code4xx.inc();
        }
        if (code > 500) {
            code5xx.inc();
        }
    }

    @Override
    public String toString() {
        return "HttpMonitor{" +
                "failCounter=" + failCounter.toString() +
                ", code4xx=" + code4xx.toString() +
                ", code5xx=" + code5xx.toString() +
                ", qpsCounter=" + qpsCounter.toString() +
                '}';
    }
}
