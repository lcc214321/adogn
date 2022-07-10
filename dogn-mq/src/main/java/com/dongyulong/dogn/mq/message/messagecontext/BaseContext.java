package com.dongyulong.dogn.mq.message.messagecontext;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础的发送context
 * @author zhangshaolong
 * @create 2021/12/29
 **/
@Data
public class BaseContext implements Serializable {

    /**
     * 支付系统订单号
     */
    private String tradeNo;

}
