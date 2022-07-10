package com.dongyulong.dogn.mq.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author 王雪源
 * @Date 2021/12/10 14:50
 * @Version 1.0
 *
 * 消息类型
 */
public enum MessageTypeEnum {

    /**
     * 交易
     */
    TRADE("trade_notify"),
    /**
     * 结算
     */
    SETTLEMENT("settlement_notify"),
    /**
     * 三方支付通知
     */
    PAY("pay_notify"),
    /**
     * 二清通知
     */
    LIQUIDATION("liquidation_notify");

    private String messageType;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    private MessageTypeEnum(String messageType){
        this.messageType = messageType;
    }

    public static Map<String, MessageTypeEnum> messageTypeEnumMap = new HashMap<>();

    static {
        MessageTypeEnum[] values = MessageTypeEnum.values();
        for (MessageTypeEnum theaMessageTypeEnum : values){
            messageTypeEnumMap.put(theaMessageTypeEnum.getMessageType(),theaMessageTypeEnum);
        }
    }

    public static MessageTypeEnum getTheaMessageType(String messageType){
        return messageTypeEnumMap.get(messageType);
    }
}
