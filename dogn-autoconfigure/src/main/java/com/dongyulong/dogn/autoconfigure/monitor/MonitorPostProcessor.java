package com.dongyulong.dogn.autoconfigure.monitor;

import com.dongyulong.dogn.core.method.DognMethod;
import com.dongyulong.dogn.core.monitor.DognMonitor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author zhangshaolong
 * @create 2021/12/27
 **/
public class MonitorPostProcessor implements BeanPostProcessor {


    /**
     * bean 初始化之前执行业务
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 启动方法的监听信息
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods.length > 0) {
            for (Method method : methods) {
                Monitor monitor = method.getAnnotation(Monitor.class);
                if (null != monitor) {
                    DognMethod dognMethod = new DognMethod(getMonitorKey(method, monitor));
                    DognMonitor.getInstance().putNotRegister(dognMethod);
                }
            }
        }
        return bean;
    }

    /**
     * 默认是方法名称key
     *
     * @param method
     * @param monitor
     * @return
     */
    private String getMonitorKey(Method method, Monitor monitor) {
        String monitorKey = monitor.value();
        if (StringUtils.isEmpty(monitorKey)) {
            String simpleName = method.getDeclaringClass().getSimpleName();
            monitorKey = simpleName + "_" + method.getName();
        }
        return monitorKey;
    }

}
