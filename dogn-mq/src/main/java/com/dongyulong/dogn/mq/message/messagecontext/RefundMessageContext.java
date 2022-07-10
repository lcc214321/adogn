package com.dongyulong.dogn.mq.message.messagecontext;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author dongy
 * @date 16:59 2022/6/8
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class RefundMessageContext extends BaseContext {
    /**
     * 支付订单号
     */
    private String payTradeNo;
    /**
     * 支付用户id
     */
    private Integer userId;
    /**
     * 支付渠道
     *
     * @see ChannelType;
     */
    private String channelType;
    /**
     * 退款金额，单位原，两位小数
     */
    private BigDecimal refundAmount;
    /**
     * 是否为三化订单
     */
    private boolean taxiqr;
    /**
     * 退款业务类型
     *
     * @see com.didapinche.orderservice.thrift.TBusinessEnum;
     */
    private String refundBusinessType;
    /**
     * 退款来源
     *
     * @see com.didapinche.orderservice.thrift.TOrderSourceEnum;
     */
    private String refundSource;
    /**
     * 退款重试次数
     */
    private int refundRetryCol;

}
