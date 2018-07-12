package com.bigdata.storm.util;

import com.bigdata.storm.constant.LogTypeConstant;
import com.bigdata.storm.dao.LogAnalyzeDao;
import com.bigdata.storm.domain.LogAnalyzeJob;
import com.bigdata.storm.domain.LogAnalyzeJobDetail;
import redis.clients.jedis.ShardedJedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 21:31
 */
public class LogAnalyzeHandler {
    //定时加载配置文件的标识
    private static boolean reloaded = false;
    //用来保存job信息，key为jobType，value为该类别下所有的任务。
    private static Map<String, List<LogAnalyzeJob>> jobMap;
    //用来保存job的判断条件，key为jobId,value为list，list中封装了很多判断条件。
    private static Map<String, List<LogAnalyzeJobDetail>> jobDetail;
    private static ShardedJedis jedis = MySharedJedisPool.getShardedJedisPool().getResource();

    static {
        jobMap = loadJobMap();
        jobDetail = loadjobDetailMap();
    }

    private static Map<String, List<LogAnalyzeJobDetail>> loadjobDetailMap() {
        //todo
        return null;
    }

    private static Map<String, List<LogAnalyzeJob>> loadJobMap() {
        Map<String, List<LogAnalyzeJob>> map = new HashMap<String, List<LogAnalyzeJob>>();
        List<LogAnalyzeJob> logAnalyzeJobList = new LogAnalyzeDao().loadJobList();
        for (LogAnalyzeJob logAnalyzeJob : logAnalyzeJobList) {
            int jobType = logAnalyzeJob.getJobType();
            if (isValidType(jobType)) {
                List<LogAnalyzeJob> jobList = map.get(jobType + "");
                if (jobList == null || jobList.size() == 0) {
                    jobList = new ArrayList<LogAnalyzeJob>();
                    map.put(jobType + "", jobList);
                }
                jobList.add(logAnalyzeJob);
            }
        }
        return map;
    }

    private static boolean isValidType(int jobType) {
        if (jobType == LogTypeConstant.BUY || jobType == LogTypeConstant.CLICK || jobType == LogTypeConstant.SEARCH || jobType == LogTypeConstant.VIEW) {
            return true;
        }
        return false;
    }

}
