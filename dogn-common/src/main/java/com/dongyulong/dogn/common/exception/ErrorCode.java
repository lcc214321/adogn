package com.dongyulong.dogn.common.exception;

/**
 * 服务错误信息
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/8 1:54 下午
 * @since v1.0
 */
public enum ErrorCode implements DognCode {
    /**
     * 服务错误
     **/
    PARAM_ERROR(102, "接口请求参数错误"),
    SERVICE_ERROR(104, "服务错误,请稍后再试"),
    SERVICE_BUSY(106, "系统繁忙，请稍后重试"),
    UPGRADE(107, "请升级到最新版本"),

    REQUEST_MORE(10043, "你访问的太频繁了，请稍后再试"),
    METHOD_NOT_FOUND(400, "方法未找到"),
    METHOD_ERROR(408, "不支持的请求方式"),
    ENV_ERROR(500, "环境异常"),
    BODY_ERROR(412, "请求信息为空"),
    ROUTING_ERROR(140000, "路由异常"),
    UNSUPPORTED_MERCHANT_ID_ERR(141203, "不支持的商户号"),
    /**
     * 商户号配置信息解析出错
     */
    CONFIG_INFO_ERROR(141006, "商户号配置信息解析出错"),
    /**
     * 商户号配置信息解析出错
     */
    CONFIG_INFO_INSERT_ERROR(141046, "商户号配置信息新增失败"),
    SIGN_ERROR(1410037, "签名错误"),
    /**
     * 不支持的方法
     */
    UNSUPPORTED_METHOD(141019, "不支持的方法"),
    /**
     * 商户信息查询mapper未初始化bean
     */
    MERCHANT_MAPPER_ERROR(1410038, " 商户信息查询mapper未初始化bean,请在主启动类上增加注解org.mybatis.spring.annotation.MapperScan(\"com.didapinche.agaue.spring.merchant.mapper\")");


    private int code;

    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return message;
    }
}
