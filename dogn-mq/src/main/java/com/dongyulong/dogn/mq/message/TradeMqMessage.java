package com.dongyulong.dogn.mq.message;

import com.dongyulong.dogn.mq.common.SaltConstants;
import com.dongyulong.dogn.mq.message.messagecontext.BaseContext;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/10 6:27 上午
 * @since v1.0
 */
public class TradeMqMessage<T extends BaseContext> implements Serializable {

    private final static String VERSION = "1.0.0";

    /**
     * 来自那个系统发送的
     */
    private String appId;

    /**
     * 消息发送时间 时间戳
     */
    private long time;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 版本号
     */
    private String version;

    /**
     * 消息体
     */
    private T context;
    /**
     * 消息ID
     */
    private String messageId;
    /**
     * 消息签名
     */
    private String sign;

    private TradeMqMessage() {

    }

    /**
     * 构造默认的方法体
     *
     * @param context
     */
    public TradeMqMessage(T context) {
        this.context = context;
        this.time = System.currentTimeMillis() / 1000;
        this.appId = SaltConstants.getAppId();
        this.version = VERSION;
        this.messageId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public T getContext() {
        return context;
    }

    public void setContext(T context) {
        this.context = context;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
