package com.bigdata.storm.domain;

import lombok.Data;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 21:07
 */
@Data
public class LogAnalyzeJobDetail {
    private int id;
    private int jobId;
    private String field;
    private String value;
    private int compare;
}
