package com.bigdata.storm.dao;

import com.bigdata.app.domian.BaseRecord;
import com.bigdata.storm.domain.LogAnalyzeJob;
import com.bigdata.storm.domain.LogAnalyzeJobDetail;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 21:58
 */
public class LogAnalyzeDao {
    private JdbcTemplate jdbcTemplate;

    public LogAnalyzeDao() {
        jdbcTemplate = new JdbcTemplate(DataSourceUtil.getDataSource());
    }

    public List<LogAnalyzeJob> loadJobList() {
        String sql = "SELECT `jobId`,`jobName`,`jobType`  FROM `log_analyze`.`log_analyze_job` WHERE STATUS= 1";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<LogAnalyzeJob>(LogAnalyzeJob.class));
    }
    public List<LogAnalyzeJobDetail> loadJobDetailList() {
        String sql = "SELECT condi.`jobId`,condi.`field`,condi.`value`,condi.`compare` " +
                " FROM `log_analyze`.`log_analyze_job` AS job " +
                " LEFT JOIN `log_analyze`.`log_analyze_job_condition` AS condi  " +
                " ON job.`jobId` = condi.`jobId` " +
                " WHERE job.`status` =1";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<LogAnalyzeJobDetail>(LogAnalyzeJobDetail.class));
    }

    public int[][] saveMinuteAppendRecord(List<BaseRecord> appendDataList) {
        String sql = "INSERT INTO `log_analyze`.`log_analyze_job_nimute_append` (`indexName`,`pv`,`uv`,`executeTime`,`createTime` ) " +
                "VALUES (?,?,?,?,?)";
        return saveAppendRecord(appendDataList, sql);
    }

    public int[][] saveHalfAppendRecord(List<BaseRecord> appendDataList) {
        String sql = "INSERT INTO `log_analyze`.`log_analyze_job_half_append` (`indexName`,`pv`,`uv`,`executeTime`,`createTime` ) " +
                "VALUES (?,?,?,?,?)";
        return saveAppendRecord(appendDataList, sql);
    }

    public int[][] saveHourAppendRecord(List<BaseRecord> appendDataList) {
        String sql = "INSERT INTO `log_analyze`.`log_analyze_job_hour_append` (`indexName`,`pv`,`uv`,`executeTime`,`createTime` ) " +
                "VALUES (?,?,?,?,?)";
        return saveAppendRecord(appendDataList, sql);
    }

    public int[][] saveDayAppendRecord(List<BaseRecord> appendDataList) {
        String sql = "INSERT INTO `log_analyze`.`log_analyze_job_day` (`indexName`,`pv`,`uv`,`executeTime`,`createTime` ) " +
                "VALUES (?,?,?,?,?)";
        return saveAppendRecord(appendDataList, sql);
    }

    public int[][] saveAppendRecord(List<BaseRecord> appendDataList, String sql) {
        return jdbcTemplate.batchUpdate(sql, appendDataList, appendDataList.size(), new ParameterizedPreparedStatementSetter<BaseRecord>(){

            public void setValues(PreparedStatement ps, BaseRecord baseRecord) throws SQLException {
                ps.setString(1, baseRecord.getIndexName());
                ps.setInt(2, baseRecord.getPv());
                ps.setLong(3, baseRecord.getUv());
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            }
        });
    }

    public List<BaseRecord> sumRecordValue(String startTime, String endTime) {
        String sql = "SELECT indexName,SUM(pv) AS pv,SUM(uv) AS uv FROM `log_analyze_job_nimute_append` " +
                " WHERE  executeTime BETWEEN  '" + startTime + "' AND '" +endTime+"' "+
                " GROUP BY indexName";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<BaseRecord>(BaseRecord.class));
    }


}
