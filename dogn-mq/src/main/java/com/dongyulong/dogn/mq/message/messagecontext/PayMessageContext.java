package com.dongyulong.dogn.mq.message.messagecontext;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 支付系统发送的消息信息
 * @author zhangshaolong
 * @create 2022/1/21
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PayMessageContext extends BaseContext {

    /**
     * 支付用户id
     */
    private Integer fromUserId;

    /**
     * 到账用户id
     */
    private Integer toUserId;

    /**
     */
    private Integer tradeType;

    /**
     * （顺风车支付，出租车支付，车主提现，乘客提现）
     */
    private String productType;

    /**
     * 支付系统订单号
     */
    private String orderId;

    /**
     * 业务订单号
     */
    private String outTradeNo;

    /**
     * 第三方的交易流水号
     */
    private String thirdTradeNo;

    /**
     * 第三方交易时间 (支付传第三方回调传的时间，退款传数据库落库时间)
     */
    private Date thirdTime;

    /**
     * @see {@link com.didapinche.finance.mq.context.enums.PayChannleEnum}
     */
    private String payChannel;
    /**
     * 具体支付方式 （H5，app，jsapi,以第三方实际为准）
     */
    private String payType;

    /**
     * 第三方金额 (元)
     */
    private BigDecimal price;

    /**
     *
     * @see {@link com.didapinche.finance.mq.context.enums.StatusEnum}
     */
    private Integer status;

    /**
     * 支付商户号
     */
    private Integer merchantNo;

    /**
     * 扩展字段不做处理直接存储
     */
    private Map<String,Object> info;

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public void setPayChannle(String payChannel) {
        this.payChannel = payChannel;
    }
}
