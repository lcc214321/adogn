package com.dongyulong.dogn.mq.core.bean;

import lombok.Data;
import org.apache.rocketmq.common.message.Message;

import java.io.Serializable;

/**
 * @author zhangshaolong
 * @create 2021/12/30
 **/
@Data
public class RedisMqBean implements Serializable {

    private Message message;

    private Integer mqType;
}
