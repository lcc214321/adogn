package com.dongyulong.dogn.mq.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author 王雪源
 * @Date 2021/12/10 14:57
 * @Version 1.0
 */
public enum StatusEnum {

    finish(1);

    private int status;

    StatusEnum(int status) {
        this.status = status;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static Map<String, StatusEnum> tradeStatusMap = new HashMap<>();

    static {
        StatusEnum[] values = StatusEnum.values();
        for (StatusEnum statusEnum : values){
            tradeStatusMap.put(statusEnum.name(), statusEnum);
        }
    }

    public static StatusEnum getStatusEnum(String tradeStatus){
        return tradeStatusMap.get(tradeStatus);
    }
}
