package com.dongyulong.dogn.mq.message.messagecontext;

import com.dongyulong.dogn.mq.enums.PayChannleEnum;
import com.dongyulong.dogn.mq.enums.StatusEnum;
import com.dongyulong.dogn.mq.enums.TradeTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @Author 王雪源
 * @Date 2021/12/27 9:56getProductType
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TheaMessageContext extends BaseContext {

    /**
     * 总业务类型
     */
    private String productGroup;
    /**
     * 支付用户id
     */
    private Integer fromUserId;
    /**
     * 到账用户id
     */
    private Integer toUserId;
    /**
     * @see {@link TradeTypeEnum}
     */
    private Integer tradeType;
    /**
     * （顺风车支付，出租车支付，车主提现，乘客提现）
     */
    private String productType;
    /**
     * 业务订单号
     */
    private String outTradeNo;
    /**
     * 第三方的交易流水号
     */
    private String thirdPayTradeNo;
    /**
     * @see {@link PayChannleEnum}
     */
    private String payChannle;
    /**
     * 具体支付方式 （H5，app，jsapi,以第三方实际为准）
     */
    private String payType;
    /**
     * 总金额 (元)
     */
    private BigDecimal orderPrice;
    /**
     * 第三方支付金额 (元)
     */
    private BigDecimal thirdPay;
    /**
     * 余额支付 (元)
     */
    private BigDecimal walletPay;
    /**
     * 优惠券抵扣 (元)
     */
    private BigDecimal couponPay;
    /**
     * @see {@link StatusEnum}
     */
    private Integer status;
    /**
     * 来源 （H5,cms,default）
     */
    private String source;
    /**
     * 支付商户号
     */
    @Deprecated
    private Integer merchantNo;

    /**
     * 支付商户号
     */
    private String merchantInfo;
    /**
     * 扩展字段不做处理直接存储
     */
    private Map<String, Object> financeInfo;

    /**
     * 扩展字段不需要存储
     * 运力外输可能添加字段
     * start_city 起点城市id
     * partner_no 合作方行程id
     * ride_id     嘀嗒行程id
     * service_fee 订单服务费
     * start_time  创建时间
     * pay_time     支付时间
     * insurance_fee 订单退款包含的服务费部分
     */
    private Map<String, Object> extraInfo;

    /**
     * 第三方交易时间 (支付传第三方回调传的时间，退款传数据库落库时间)
     */
    private Date payTime;

    public TheaMessageContext setTradeType(Integer tradeType) {
        if (!TradeTypeEnum.validateType(tradeType)) {
            throw new IllegalArgumentException("tradeType err");
        }
        this.tradeType = tradeType;
        return this;
    }

    public TheaMessageContext setPayChannle(PayChannleEnum payChannleEnum) {
        if (payChannleEnum != null) {
            this.payChannle = payChannleEnum.getPayChannle();
        }
        return this;
    }

    public TheaMessageContext setPayChannleStr(String payChannle) {
        this.payChannle = payChannle;
        return this;
    }
}
