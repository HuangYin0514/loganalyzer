package com.bigdata.storm.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 21:46
 */
public class DataSourceUtil {
    private static DataSource dataSource;

    static {
        dataSource = new ComboPooledDataSource("logAnalyze");
    }

    public static synchronized DataSource getDataSource(){
        if (dataSource == null) {
            dataSource = new ComboPooledDataSource();
        }
        return dataSource;
    }
}
