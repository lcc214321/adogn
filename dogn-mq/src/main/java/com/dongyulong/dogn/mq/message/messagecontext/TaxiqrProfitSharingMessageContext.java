package com.dongyulong.dogn.mq.message.messagecontext;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author dongy
 * @date 15:28 2022/6/10
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class TaxiqrProfitSharingMessageContext extends BaseContext {
    /**
     * 支付订单号
     */
    private String payTradeNo;
    /**
     * 司机姓名
     */
    private String driverName;
    /**
     * 司机微信openId
     */
    private String driverWxOpenId;
    /**
     * 司机id
     */
    private Integer driverId;
    /**
     * 发起支付的用户id
     */
    private Integer payUserId;

}
