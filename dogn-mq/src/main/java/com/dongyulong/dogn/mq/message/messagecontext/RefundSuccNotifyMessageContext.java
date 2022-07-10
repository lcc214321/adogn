package com.dongyulong.dogn.mq.message.messagecontext;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author dongy
 * @date 18:18 2022/6/8
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class RefundSuccNotifyMessageContext extends BaseContext {
    /**
     * 业务侧退款订单号
     */
    private String payTradeNo;
    /**
     * 商户退款订单号
     */
    private String paymentRefundNo;
    /**
     * 三方退款订单号,不一定有
     */
    private String thirdRefundNo;
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    /**
     * 实际退款金额
     */
    private BigDecimal actualRefundAmount;
    /**
     * 退款时间,同步三方时间
     */
    private Date refundTime;
}