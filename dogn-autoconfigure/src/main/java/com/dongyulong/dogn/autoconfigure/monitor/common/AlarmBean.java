package com.dongyulong.dogn.autoconfigure.monitor.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 报警的一些数据信息
 * @author zhangshaolong
 * @create 2022/1/25
 **/
@Data
public class AlarmBean implements Serializable {

    private int slow = 100;

    private double alarm = 0.5;

    private int monitor = 1000;


    public static AlarmBean getDefault() {
        AlarmBean alarmBean = new AlarmBean();
        alarmBean.setAlarm(0.5);
        alarmBean.setMonitor(100);
        alarmBean.setSlow(100);
        return alarmBean;
    }
}
