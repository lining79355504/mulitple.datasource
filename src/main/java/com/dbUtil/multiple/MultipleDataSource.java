package com.dbUtil.multiple;

import com.dbUtil.handle.MultipleDataSourceHandler;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Author:  lining17
 * Date :  28/02/2018
 */
public class MultipleDataSource  extends AbstractRoutingDataSource implements ApplicationContextAware , BeanDefinitionRegistryPostProcessor{

    private static ApplicationContext context ;

    private static final ThreadLocal<String> dataSourceKey = new InheritableThreadLocal<String>();


    public MultipleDataSource(){

    }

    public void init(){
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) this.context;
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory();
        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(MultipleDataSourceHandler.class);    //设置类
        definition.setScope("singleton");       //设置scope
        definition.setLazyInit(false);          //设置是否懒加载
        definition.setAutowireCandidate(true);  //设置是否可以被其他对象自动注入
        beanDefinitionRegistry.registerBeanDefinition("multipleDataSourceHandler", definition);

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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        init();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        List<String> proList = Arrays.asList("a*","b*","c*","d*",
                "e*","f*","g*","p*","h*","i*","j*","k*","l*","m*","n*","o*","q*","r*","s*","t*","w*","u*","v*","z*");
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("dataSourceSwitchAutoProxy");
        MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
        mutablePropertyValues.add("beanNames",proList);
        mutablePropertyValues.add("interceptorNames" , "multipleDataSourceHandler");
    }
}
