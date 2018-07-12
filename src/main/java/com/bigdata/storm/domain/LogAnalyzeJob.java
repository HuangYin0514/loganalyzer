package com.bigdata.storm.domain;

import lombok.Data;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 21:06
 */
@Data
public class LogAnalyzeJob {
    private String jobId ;
    private String jobName;
    private int jobType; //1:浏览日志、2:点击日志、3:搜索日志、4:购买日志
    private int bussinessId;
    private int status;


}
