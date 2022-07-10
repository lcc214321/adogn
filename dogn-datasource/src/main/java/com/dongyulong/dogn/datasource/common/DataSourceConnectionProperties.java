package com.dongyulong.dogn.datasource.common;

import lombok.Data;

/**
 * @author dongy
 * @version v2.0.1
 * @date 17:46 2022/1/6
 **/
@Data
public class DataSourceConnectionProperties {

    private String url;

    private String username;

    private String password;
}
