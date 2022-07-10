package com.dongyulong.dogn.mq.message.messagecontext;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dongy
 * @date 17:10 2022/6/15
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class ActiveQueryMessageContext extends BaseContext {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 出租车，顺风车，商城等业务场景的交易订单号
     */
    private String payTradeNo;
    /**
     * 是否为三化
     */
    private Boolean taxiqr = Boolean.FALSE;

    public Boolean getTaxiqr() {
        return taxiqr != null && taxiqr;
    }

}
