package com.bigdata.storm.dao;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 22:17
 */
public class RecordBatchPreParedStatementSetter implements BatchPreparedStatementSetter {

    private Map<String, Map<String, Object>> appData;

    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {


    }

    public int getBatchSize() {
        return appData.get("pv").size();
    }
}
