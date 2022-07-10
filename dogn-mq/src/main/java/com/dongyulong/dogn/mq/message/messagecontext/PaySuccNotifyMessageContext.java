package com.dongyulong.dogn.mq.message.messagecontext;

import java.math.BigDecimal;
import java.util.Date;

public class PaySuccNotifyMessageContext extends BaseContext {

    /**
     * 发起支付商户侧的订单号，根据payment_order#id生成
     */
    private String payTradeNo;
    /**
     * 第三方支付订单号
     */
    private String thirdpartyOrderNo;

    /**
     * 支付总金额
     */
    private BigDecimal paymentAmount;
    /**
     * 实际支付的金额
     */
    private BigDecimal actualPaymentAmount;

    /**
     * 第三方支付账户ID
     */
    private String thirdpartyUserId;

    /**
     * 扩展信息
     */
    private String paymentExtra;

    /**
     * 第三方支付时间
     */
    private Date payTime;


    /**
     * 一级商户号id
     */
    private String merchantId;

    /**
     * 二级商户号id
     */
    private String subMerchantId;


    public String getPayTradeNo() {
        return payTradeNo;
    }

    public void setPayTradeNo(String payTradeNo) {
        this.payTradeNo = payTradeNo;
    }

    public String getThirdpartyOrderNo() {
        return thirdpartyOrderNo;
    }

    public void setThirdpartyOrderNo(String thirdpartyOrderNo) {
        this.thirdpartyOrderNo = thirdpartyOrderNo;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getActualPaymentAmount() {
        return actualPaymentAmount;
    }

    public void setActualPaymentAmount(BigDecimal actualPaymentAmount) {
        this.actualPaymentAmount = actualPaymentAmount;
    }

    public String getThirdpartyUserId() {
        return thirdpartyUserId;
    }

    public void setThirdpartyUserId(String thirdpartyUserId) {
        this.thirdpartyUserId = thirdpartyUserId;
    }

    public String getPaymentExtra() {
        return paymentExtra;
    }

    public void setPaymentExtra(String paymentExtra) {
        this.paymentExtra = paymentExtra;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getSubMerchantId() {
        return subMerchantId;
    }

    public void setSubMerchantId(String subMerchantId) {
        this.subMerchantId = subMerchantId;
    }


}
