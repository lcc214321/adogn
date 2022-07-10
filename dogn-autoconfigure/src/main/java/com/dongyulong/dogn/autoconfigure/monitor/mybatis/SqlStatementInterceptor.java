package com.dongyulong.dogn.autoconfigure.monitor.mybatis;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/86:32 下午
 * @since v1.0
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class})})
public class SqlStatementInterceptor implements Interceptor {

    private final Logger logger = LoggerFactory.getLogger(SqlStatementInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startNanos = System.nanoTime();
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        String mapperMethod = ms.getId();
        try {
            return invocation.proceed();
        } catch (Exception e) {
            logger.error("执行失败！", e);
            throw new Exception(e);
        } finally {
            SqlMetrics.recordToPrometheus(mapperMethod, startNanos);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
