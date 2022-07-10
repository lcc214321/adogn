package com.dongyulong.dogn.datasource.common;

import lombok.Data;

import java.util.Map;

/**
 * @author dongy
 * @version v2.0.1
 * @date 17:47 2022/1/6
 **/
@Data
public class ShardingConnectionProperties {

    private String database;

    private String username;

    private String password;

    private String url;

    private Map<String, String> shards;
}
