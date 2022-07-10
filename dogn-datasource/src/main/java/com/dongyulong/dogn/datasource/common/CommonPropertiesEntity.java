package com.dongyulong.dogn.datasource.common;

import lombok.Data;

/**
 * @author dongy
 * @version v2.0.1
 * @date 12:57 2022/1/6
 **/
@Data
public class CommonPropertiesEntity {

    private DataSourceCommonProperties master;

    private DataSourceCommonProperties slave;

}
