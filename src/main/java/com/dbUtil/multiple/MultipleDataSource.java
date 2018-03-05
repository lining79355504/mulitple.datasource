package com.dbUtil.multiple;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.logging.Logger;

/**
 * Author:  lining17
 * Date :  28/02/2018
 */
public class MultipleDataSource extends AbstractRoutingDataSource {

    private static ApplicationContext context=new
            ClassPathXmlApplicationContext("classpath*:/*.xml");
    private static ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) context;
    private static BeanDefinitionRegistry beanDefinitionRegistry = (DefaultListableBeanFactory) configurableContext.getBeanFactory();


    private static final ThreadLocal<String> dataSourceKey = new InheritableThreadLocal<String>();


    public void MultipleDataSource(){
        init();
    }

    public void init(){
        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
        beanNameAutoProxyCreator.setBeanNames();
        beanNameAutoProxyCreator.setInterceptorNames();
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(BeanNameAutoProxyCreator.class);
        BeanDefinition beanDefinition=beanDefinitionBuilder.getBeanDefinition();
        beanDefinitionRegistry.registerBeanDefinition("dataSourceSwitchAutoProxy",beanDefinition);
    }


    public static void setDataSourceKey(String dataSource){

        dataSourceKey.set(dataSource);

    }

    @Override
    protected Object determineCurrentLookupKey() {
        return dataSourceKey.get();
    }

    public static String getCurrentDatasource(){
        return dataSourceKey.get();
    }

    @Override
    public Logger getParentLogger() {
        return null;
    }
}
