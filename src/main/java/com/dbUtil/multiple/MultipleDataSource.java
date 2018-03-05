package com.dbUtil.multiple;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.logging.Logger;

/**
 * Author:  lining17
 * Date :  28/02/2018
 */
public class MultipleDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> dataSourceKey = new InheritableThreadLocal<String>();


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
