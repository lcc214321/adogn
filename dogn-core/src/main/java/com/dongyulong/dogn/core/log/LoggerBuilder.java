package com.dongyulong.dogn.core.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.OptionHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 手动构造log日志信息
 *
 * @author zhangshaolong
 * @create 2022/2/15
 **/
public class LoggerBuilder {

    private static final ConcurrentHashMap<String, Logger> LOG_FACTORY = new ConcurrentHashMap<>();

    private static final Object lockObject = new Object();


    /**
     * 获取日志，默认是info级别,自定义日志文件
     *
     * @param name
     * @return
     */
    public static Logger getLogger(String name) {
        return getLogger(name, Level.INFO);
    }

    /**
     * 获取日志信息
     *
     * @param name
     * @param level
     * @return
     */
    public static Logger getLogger(String name, Level level) {
        Logger logger = LOG_FACTORY.get(name);
        if (logger != null) {
            return logger;
        }
        synchronized (lockObject) {
            logger = LOG_FACTORY.get(name);
            if (logger != null) {
                return logger;
            }
            logger = build(name, level);
            LOG_FACTORY.put(name, logger);
        }
        return logger;
    }

    /**
     * <logger name="SlowLogger" level="info" additivity="false">
     * <appender-ref ref="FILE_SLOW"/>
     * </logger>
     * 设置log信息
     *
     * @param name
     * @return
     */
    private static Logger build(String name, Level level) {
        //配置输出信息
        RollingFileAppender rollingFileAppender = createRollingFileAppender(name, level);
        //配置控制台信息
        ConsoleAppender consoleAppender = createConsoleAppender();
        //设置最外层的logger
        ch.qos.logback.classic.Logger logger = buildLogger(name, level);
        //判断环境信息
        logger.addAppender(rollingFileAppender);
        //设置控制台输出
        //logger.addAppender(consoleAppender);
        return logger;
    }


    /**
     * <logger name="SlowLogger" level="info" additivity="false">
     * <appender-ref ref="FILE_SLOW"/>
     * </logger>
     * 获取log信息
     *
     * @return
     */
    private static ch.qos.logback.classic.Logger buildLogger(String name, Level level) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = context.getLogger(name);
        logger.setAdditive(false);
        logger.setLevel(level);
        return logger;
    }

    /**
     * <appender name="FILE_ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
     * <file>${logDir}/${appName}_error.log</file>
     * <encoder>
     * <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
     * </encoder>
     * <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
     * <fileNamePattern>${logDir}/${appName}_error.%d{yyyy-MM-dd}.log</fileNamePattern>
     * <maxHistory>30</maxHistory>
     * </rollingPolicy>
     * <filter class="ch.qos.logback.classic.filter.LevelFilter">
     * <level>ERROR</level>
     * <onMatch>ACCEPT</onMatch>
     * <onMismatch>DENY</onMismatch>
     * </filter>
     * </appender>
     * 设置roolingfileappender appender文件
     *
     * @param name
     * @param level
     * @return
     */
    public static RollingFileAppender createRollingFileAppender(String name, Level level) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        RollingFileAppender appender = new RollingFileAppender();
        appender.setName(name + "_" + level.levelStr);
        appender.setFile(OptionHelper.substVars(getLogPath() + "_" + name + ".log", context));
        //设置过滤
        appender.addFilter(createLevelFilter(level));
        //设置编码
        appender.setEncoder(createEncoder(context));
        appender.setContext(context);
        //设置备份
        appender.setRollingPolicy(createTimeBasedRollingPolicy(name, context, appender));
        appender.start();
        return appender;
    }

    public static ConsoleAppender createConsoleAppender() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ConsoleAppender appender = new ConsoleAppender();
        appender.setContext(context);
        appender.setName("console");
        appender.setEncoder(createEncoder(context));
        appender.start();
        return appender;
    }

    /**
     * 时间备份
     * <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
     * <fileNamePattern>${logDir}/${appName}_error.%d{yyyy-MM-dd}.log</fileNamePattern>
     * <maxHistory>30</maxHistory>
     * </rollingPolicy>
     *
     * @param name
     * @param context
     * @param appender
     * @return
     */
    private static TimeBasedRollingPolicy createTimeBasedRollingPolicy(String name, LoggerContext context, FileAppender appender) {
        TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
        String fp = OptionHelper.substVars(getLogPath() + "_" + name + ".%d{yyyy-MM-dd}.log", context);
        policy.setFileNamePattern(fp);
        policy.setMaxHistory(30);
        policy.setParent(appender);
        policy.setContext(context);
        policy.start();
        return policy;
    }

    /**
     * 设置编码类似
     * <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
     * <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
     * <charset>UTF-8</charset>
     * </encoder>
     *
     * @param context
     * @return
     */
    private static PatternLayoutEncoder createEncoder(LoggerContext context) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] [%X{traceId}] %-5level %logger{36} - %msg%n");
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.start();
        return encoder;
    }


    /**
     * <filter class="ch.qos.logback.classic.filter.LevelFilter">
     * <level>INFO</level>
     * <onMatch>ACCEPT</onMatch>
     * <onMismatch>DENY</onMismatch>
     * </filter>
     * <p>
     * 对应的配置信息,防止打印上层级别的信息
     *
     * @param level
     * @return
     */
    private static LevelFilter createLevelFilter(Level level) {
        LevelFilter levelFilter = new LevelFilter();
        levelFilter.setLevel(level);
        levelFilter.setOnMatch(FilterReply.ACCEPT);
        levelFilter.setOnMismatch(FilterReply.DENY);
        levelFilter.start();
        return levelFilter;
    }

//    -Dapp.name=xxxx -Dlog.path=xxxx

    private static String getAppName() {
        return System.getProperty("app.name");
    }

    private static String getLogPath() {
        String path = System.getProperty("log.path");
        if (StringUtils.isEmpty(path)) {
            //获取当前路径的信息
            path = System.getProperty("user.dir");
        }
        if (!path.endsWith("/")) {
            path += "/";
        }
        String appName = getAppName();
        if (StringUtils.isEmpty(appName)) {
            appName = "test";
        }
        return path + appName;
    }

}
