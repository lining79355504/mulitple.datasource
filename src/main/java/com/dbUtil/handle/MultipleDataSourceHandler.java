package com.dbUtil.handle;

import com.dbUtil.annotation.DataSource;
import com.dbUtil.multiple.MultipleDataSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Author:  mort
 * Date :  05/03/2018
 */
@Qualifier("multipleDataSourceHandler")
public class MultipleDataSourceHandler implements MethodInterceptor, InitializingBean,ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(MultipleDataSourceHandler.class);

    ApplicationContext applicationContext = null ;

    public Object invoke(MethodInvocation invocation) throws Throwable {


        Method method = invocation.getMethod();
        DataSource dataSource = AnnotationUtils.findAnnotation(method, DataSource.class);
        if (null == dataSource) {
            return invocation.proceed();
        }


        String dataSourceStr = dataSource.value();

        if (StringUtils.isEmpty(dataSourceStr)) {
            logger.warn("use default dataSource ");
        }

        if (method.isAnnotationPresent(DataSource.class)) {
            MultipleDataSource.setDataSourceKey(dataSourceStr);
        }

        Object result = invocation.proceed();

        MultipleDataSource.clearDataSourceKey();

        return result;
    }


    public void afterPropertiesSet() throws Exception {

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
