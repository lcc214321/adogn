package com.dongyulong.dogn.table.eg;

import com.dongyulong.dogn.table.common.ITableNo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author dongy
 * @date 13:27 2022/3/31
 **/
@Getter
@RequiredArgsConstructor
public enum TableIdEnum implements ITableNo {

    PAYMENT_ORDER(0L),
    PAYMENT_REFUND(1L),
    PAYMENT_TRANSFER(2L),
    TAXIQR_PAYMENT_ORDER(4L),
    TAXIQR_PAYMENT_TRANSFER(5L),
    TAXIQR_PAYMENT_REFUND(6L),
    INSURANCE_ORDER(7L);
    /**
     * 表id
     * 仅支持0-63的tableid
     */
    private final Long no;

}
