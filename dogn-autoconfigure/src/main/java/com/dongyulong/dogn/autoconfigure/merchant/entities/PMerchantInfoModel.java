package com.dongyulong.dogn.autoconfigure.merchant.entities;

import lombok.Data;

import java.util.Date;

/**
 * 商户信息表
 *
 * @author dongy
 * @date 21:49 2022/2/10
 **/
@Data
public class PMerchantInfoModel {
    /**
     * 主键索引
     */
    private Integer id;
    /**
     * varchar(30) NOT NULL COMMENT '渠道类型',
     */
    private String channelType;
    /**
     * varchar(24) NOT NULL COMMENT '三方支付渠道分配的商户号，如果没有可根据需要设置为app_id或id',
     */
    private String mchNo;
    /**
     * varchar(24) NOT NULL COMMENT '父级商户号',
     */
    private String preMchNo;
    /**
     * varchar(30) NOT NULL COMMENT '商户唯一编号，跟channel_type+mch_no唯一对应',
     */
    private String mchUniqueNo;
    /**
     * varchar(32) DEFAULT NULL COMMENT '应用id,如果存在同一套配置信息多个app_id,需要关联表中定义出app_id',
     */
    private String appId;
    /**
     * varchar(32) NOT NULL COMMENT '商户全称'
     */
    private String mchName;
    /**
     * varchar(5000) DEFAULT NULL COMMENT '微信、支付宝等渠道的商户配置信息json格式加密',
     */
    private String config;
    /**
     * char(11) NOT NULL COMMENT '申请人'
     */
    private String owner;
    /**
     * datetime NOT NULL COMMENT ' 创建时间',
     */
    private Date createTime;
    /**
     * datetime NOT NULL COMMENT '更新时间',
     */
    private Date updateTime;
}
