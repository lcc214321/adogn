package com.dongyulong.dogn.datasource.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 需要开启的数据库名称
 *
 * @author dongy
 * @version v2.0.1
 * @date 14:04 2022/1/4
 **/
@Getter
@RequiredArgsConstructor
public enum DatabaseEnum implements IDatabase {

    payment("payment"),
    didapinche("didapinche"),
    payaccounthis("payaccounthis"),
    coupon("coupon"),
    carpool("carpool"),
    taxi("taxi"),
    beike("beike"),
    jifen("jifen"),
    liquidation("liquidation"),
    bus("bus"),
    enterprise("enterprise"),
    cps("cps"),
    cms("cms"),
    h5("h5"),
    saas("saas"),
    medal("medal"),
    ridehis("ridehis"),
    finance("finance"),
    withdraw("withdraw"),
    misc("misc"),
    paymenttaxiqr("payment_taxiqr"),
    order("order");

    private final String key;
}
