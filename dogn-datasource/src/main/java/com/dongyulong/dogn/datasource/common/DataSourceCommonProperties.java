package com.dongyulong.dogn.datasource.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dongy
 * @version v2.0.1
 * @date 17:46 2022/1/6
 **/
@Data
public class DataSourceCommonProperties {

    private String driverClassName;

    private int initialSize;

    private int minIdle;

    private int maxActive;

    private int maxWait;

    private Boolean defaultAutoCommit;

    protected boolean removeAbandoned;

    /**
     * 单位是秒
     */
    protected int removeAbandonedTimeout;

    private int timeBetweenEvictionRunsMillis;

    private int minEvictableIdleTimeMillis;

    private String validationQuery;

    private boolean testWhileIdle;

    private boolean testOnBorrow;

    private boolean testOnReturn;

    private boolean poolPreparedStatements;

    private int maxPoolPreparedStatementPerConnectionSize;

    private String filters;

    private String connectionProperties;

    private List<String> connectionInitSqls = new ArrayList<>();

}
