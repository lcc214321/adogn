package com.dongyulong.dogn.datasource.common;

import lombok.Data;

/**
 * @author dongy
 * @version v2.0.1
 * @date 11:06 2022/1/4
 **/
@Data
public class ConnectionPropertiesEntity {

    private DataSourceConnectionProperties master;

    private DataSourceConnectionProperties slave;

    private DataSourceConnectionProperties tidb;

    private DataSourceConnectionProperties bdc;

}
