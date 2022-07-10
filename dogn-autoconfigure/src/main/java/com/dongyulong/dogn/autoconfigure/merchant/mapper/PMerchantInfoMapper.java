package com.dongyulong.dogn.autoconfigure.merchant.mapper;


import com.dongyulong.dogn.autoconfigure.merchant.entities.PMerchantInfoModel;

import java.util.List;

/**
 * @author dongy
 * @date 18:31 2022/2/11
 **/
public interface PMerchantInfoMapper {

    /**
     * 新增数据
     *
     * @param insertContent -
     * @return -
     */
    int insertSelective(PMerchantInfoModel insertContent);

    /**
     * 根据条件查询单挑数据
     *
     * @param condition -
     * @return -
     */
    PMerchantInfoModel selectOneByCondition(PMerchantInfoModel condition);

    /**
     * 查询全部数据
     *
     * @return -
     */
    List<PMerchantInfoModel> selectListNoCondition();

    /**
     * 根据渠道查询配置信息
     *
     * @param channelType -
     * @return -
     */
    List<PMerchantInfoModel> selectListByChannel(String channelType);

}




