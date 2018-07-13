package com.bigdata.app.domian;

import lombok.Data;
import lombok.extern.java.Log;

import java.lang.annotation.Annotation;
import java.util.Date;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 22:03
 */
@Data
public class BaseRecord {
    private String indexName;
    private String redisKey;
    private int pv;
    private long uv;
    private Date processTime;

    public BaseRecord(String jobName, int pv, long uv, Date processTime) {
        this.indexName = jobName;
        this.pv = pv;
        this.uv = uv;
        this.processTime = processTime;
    }
}
