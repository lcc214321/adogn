package com.dongyulong.dogn.datasource.autoconfigure.sqlsessionfactory;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

import javax.sql.DataSource;

/**
 * @author dongy
 * @date 17:24 2022/1/20
 **/
public interface SqlSessionFactoryProvider {

    /**
     * buildSqlSessionFactory
     *
     * @param dataSource -
     * @return -
     * @throws IllegalAccessException -
     */
    SqlSessionFactory buildSqlSessionFactory(DataSource dataSource) throws IllegalAccessException;

    /**
     * buildSqlSessionFactory
     *
     * @param sqlSessionFactory -
     * @return -
     */
    SqlSessionTemplate buildSqlSessionTemplate(SqlSessionFactory sqlSessionFactory);
}
