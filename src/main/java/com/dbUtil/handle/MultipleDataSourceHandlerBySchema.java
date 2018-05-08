package com.dbUtil.handle;

import com.dbUtil.annotation.DataSource;
import com.dbUtil.multiple.MultipleDataSource;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Author:  mort
 * Date :  07/05/2018
 *
 * 只能实现spring启动时bean初始化的 拦截和操作 不能实现method动态切入,还是要通过动态切面或者重新生成代理类实现。
 * 自定义标签<multiDataSource:annotation-driven/>
 * 自定义xml schema
 * 1：通过 <multiDataSource:annotation-driven/>  xsd xsd内置元素 schema 实现spring bean的上下文扫描
 * 2：通过实现BeanPostProcessor BeanFactoryAware 实现
 * 3：ReflectionUtils.doWithMethods实现对方法层面的注解拦截  不用配置xml 或者注解切面实现
 *
 */
public class MultipleDataSourceHandlerBySchema implements BeanPostProcessor, Ordered,
        ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware, BeanFactoryAware {

    private ApplicationContext applicationContext;


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);

        DataSource dataSource = AnnotationUtils.findAnnotation(targetClass, DataSource.class);  //获取class 注解

//        DataSource dataSource = AnnotationUtils.getAnnotation(method, DataSource.class);  获取方法注解

        ReflectionUtils.doWithMethods(targetClass , new ReflectionUtils.MethodCallback(){

            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                DataSource dataSource = AnnotationUtils.getAnnotation(method, DataSource.class);
                if(null == dataSource.value()){
                    return;
                }

                MultipleDataSource.setDataSourceKey(dataSource.value());
            }
        });


        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
