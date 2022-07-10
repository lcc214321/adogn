package com.dongyulong.dogn.autoconfigure.log;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.text.CharSequenceUtil;
import com.dongyulong.dogn.common.exception.DognCode;
import com.dongyulong.dogn.common.exception.IgnoreException;
import com.dongyulong.dogn.core.annotation.LogOpen;
import com.dongyulong.dogn.core.annotation.UseLogger;
import com.dongyulong.dogn.core.log.LoggerBuilder;
import com.dongyulong.dogn.tools.json.JsonMapper;
import com.google.common.collect.Lists;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 打印方法(不适用静态方法)开始和结束的日志，
 * 输出方法名（或指定的业务表示{@link LogOpen}）和方法参数，方法的注解优先级高于类的注解{@link LogOpen}，
 * 打印私有方法需要将注解{@link LogOpen#open}设置为true,类上设置无效
 *
 * @author dongy/和小奇
 * @date 2019/2/22 10:38 AM
 * @see LogOpen
 * @see UseLogger
 */

public class MethodLogAroundHandler {

    private static final Logger LOGGER = LoggerBuilder.getLogger("access");


    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 缓存日志模板
     * className.method start. parameter1:value1,parameter2:value2 ...
     * flag{开始/结束}. parameter1:{},parameter2:{},parameter3:{} ...
     */
    private final Map<String, String> methodParameterTemplate = new ConcurrentHashMap<>(64);

    /**
     * 没有开启日志的方法
     */
    private final Set<String> methodWithoutLog = new ConcurrentHashSet<>(64);

    /**
     * 基本数据类型及其包装类型
     */
    private static final Set<String> TYPES = new HashSet<>(Arrays.asList(
            byte.class.getTypeName(), Byte.class.getTypeName(),
            short.class.getTypeName(), Short.class.getTypeName(),
            int.class.getTypeName(), Integer.class.getTypeName(),
            long.class.getTypeName(), Long.class.getTypeName(),
            float.class.getTypeName(), Float.class.getTypeName(),
            double.class.getTypeName(), Double.class.getTypeName(),
            boolean.class.getTypeName(), Boolean.class.getTypeName(),
            char.class.getTypeName(), Character.class.getTypeName(),
            String.class.getTypeName()));


    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        // 获取对象、方法、参数
        Object target = pjp.getTarget();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Object[] args = pjp.getArgs();
        //方法名
        String methodName = pjp.getTarget().getClass().getSimpleName() + "." + signature.getMethod().getName();
        //查询不打印日志的白名单
        if (methodWithoutLog.contains(methodName)) {
            return pjp.proceed(args);
        }
        //查询当前方法是否开启日志
        if (!isPrintLog(target, method)) {
            return pjp.proceed(args);
        }
        //获取开始日志模板
        String startTemplate = methodParameterTemplate.computeIfAbsent(methodName, key -> this.generateLogTemplate(target, method));
        List<String> params = processArgs(args);
        // 打印开始日志
        Object result;
        try {
            result = pjp.proceed(args);
            //无返回值或返回值为空
            if (result == null) {
                params.set(0, "result *** return void or null *** ");
                LOGGER.info(startTemplate, params.toArray(new Object[0]));
                return null;
            }
            //打印返回值不为空的日志
            params.add(TYPES.contains(result.getClass().getTypeName()) ? result.toString() : JsonMapper.toJson(result));
            String endTemplateBuilder = startTemplate + " ^^^ result:{}";
            params.set(0, " result ");
            LOGGER.info(endTemplateBuilder, params.toArray(new Object[0]));
            return result;
        } catch (IgnoreException e) {
            //打印异常结束日志
            DognCode errorCode = e.getErrorCode();
            if (errorCode != null) {
                params.set(0, CharSequenceUtil.format(" warn end code:{},message:{}", errorCode.getCode(), errorCode.getMsg()));
            } else {
                params.set(0, " warn ");
            }
            LOGGER.warn(CharSequenceUtil.format(startTemplate, params.toArray(new Object[0])));
            throw e;
        } catch (Throwable throwable) {
            //打印异常结束日志
            params.set(0, " error ");
            LOGGER.error(CharSequenceUtil.format(startTemplate, params.toArray(new Object[0])), throwable);
            throw throwable;
        }
    }

    /**
     * 校验方法是否开启了日志
     *
     * @param targetInstance 当前实例
     * @param method         当前实例方法
     * @return -
     */
    private boolean isPrintLog(Object targetInstance, Method method) {
        String methodName = targetInstance.getClass().getSimpleName() + "." + method.getName();
        if (methodWithoutLog.contains(methodName)) {
            return false;
        }
        //方法上的注解优先级最高
        LogOpen methodLogOpen = method.getDeclaredAnnotation(LogOpen.class);
        if (methodLogOpen != null) {
            if (!methodLogOpen.open()) {
                methodWithoutLog.add(methodName);
            }
            return methodLogOpen.open();
        }
        //类注解时private方法不打印日志
        if (Modifier.isPrivate(method.getModifiers())) {
            methodWithoutLog.add(methodName);
            return false;
        }
        //检查类上注解
        LogOpen classLogOpen = targetInstance.getClass().getDeclaredAnnotation(LogOpen.class);
        boolean classOpen = classLogOpen != null && classLogOpen.open();
        if (!classOpen) {
            methodWithoutLog.add(methodName);
        }
        return classOpen;
    }


    /**
     * 生成方法日志的模板
     * 如果{@link LogOpen#value()} 未指定了日志前缀，则使用 {@param methodName}
     * <p>
     * [weixinPayHandler].[method] ***weixin*** {start/end} .parma1:{},parma2:{},parma3:{}
     *
     * @param target 目标对象
     * @param method 目标方法
     */
    private String generateLogTemplate(Object target, Method method) {
        //方法全名
        String methodName = target.getClass().getSimpleName() + "." + method.getName();
        // 第一次调用创建日志模板
        LogOpen methodDeclaredAnnotation = method.getDeclaredAnnotation(LogOpen.class);
        LogOpen classDeclaredAnnotation = target.getClass().getDeclaredAnnotation(LogOpen.class);
        StringBuilder logTemplateBuilder = new StringBuilder(methodName);
        if (methodDeclaredAnnotation != null && StringUtils.isNotBlank(methodDeclaredAnnotation.value())) {
            logTemplateBuilder.insert(0, methodDeclaredAnnotation.value() + " ");
        } else if (classDeclaredAnnotation != null && StringUtils.isNotBlank(classDeclaredAnnotation.value())) {
            logTemplateBuilder.insert(0, classDeclaredAnnotation.value() + " ");
        }
        HandlerMethod handlerMethod = new HandlerMethod(target, method);
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        // 开始/结束
        logTemplateBuilder.append(" {} ");
        for (MethodParameter methodParameter : methodParameters) {
            methodParameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            logTemplateBuilder.append(methodParameter.getParameterName())
                    .append(":")
                    .append("{}")
                    .append(",");
        }
        logTemplateBuilder.deleteCharAt(logTemplateBuilder.length() - 1);
        return logTemplateBuilder.toString();
    }


    /**
     * 将参数转化为String
     *
     * @param args 方法入参
     * @return -
     */
    private List<String> processArgs(Object[] args) {
        List<String> params = Lists.newArrayList();
        params.add("start");
        if (ArrayUtils.isEmpty(args)) {
            return params;
        }
        Arrays.stream(args).forEach(object -> {
            if (object == null) {
                params.add(null);
                return;
            }
            if (isServlet(object)) {
                params.add("servlet no exhibit");
                return;
            }
            Class<?> aClass = object.getClass();
            if (TYPES.contains(aClass.getTypeName())) {
                params.add(object.toString());
                return;
            }
            params.add(JsonMapper.toJson(object));
        });
        return params;
    }

    private static boolean isServlet(@NonNull Object val) {
        return (val instanceof HttpServletRequest) || (val instanceof HttpServletResponse);
    }
}
