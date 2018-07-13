package com.bigdata.storm.util;

import com.bigdata.storm.constant.LogTypeConstant;
import com.bigdata.storm.dao.LogAnalyzeDao;
import com.bigdata.storm.domain.LogAnalyzeJob;
import com.bigdata.storm.domain.LogAnalyzeJobDetail;
import com.bigdata.storm.domain.LogMessage;
import com.google.gson.Gson;
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
        jobDetail = loadJobDetailMap();
    }

    /**
     * 将json串转化成javaBean对象
     *
     * @param line
     * @return
     */
    public static LogMessage parser(String line) {
        LogMessage logMessage = new Gson().fromJson(line, LogMessage.class);
        return logMessage;
    }

    /**
     * 判断该条日志是否符合计算规则
     * 将符合规则的日志加入到redis进行统计
     *
     * @param logMessage
     */
    public static void process(LogMessage logMessage) {
        if (jobMap == null || jobDetail == null) {
            loadDataModel();
        }
        // kafka来的日志：2,req,ref,xxx,xxx,xxx,yy
        //1\浏览----->网站中有多少个静态页面，或者多少个商品详情页，就有多少个指标。
        //2\标签-点击--->成千万上万的标签，是否需要每个标签都统计？产品经理配置多少就是多少个？
        //假设有5000个Job
        List<LogAnalyzeJob> analyzeJobList = jobMap.get(logMessage.getType() + "");
        //根据数据类型分发之后，3000个Job
        //如果每个Job都有5个判断条件，请问，针对一次消息要判断多少次？3000*5=1.5w次判断。
        //如果不满足的Job都在2个条件之后终端，实际判断6000次。
        //一天1T的数据，计算量是否特别大？
        //一天1T的数据中，1亿， 3000万条需要被计算。
        //每条中计算多少条？3000万/24h=125万/60m= 2万/60s=333条
        for (LogAnalyzeJob logAnalyzeJob : analyzeJobList) {
            boolean isMatch = false;
            List<LogAnalyzeJobDetail> logAnalyzeJobDetailList = jobDetail.get(logAnalyzeJob.getJobId());
            for (LogAnalyzeJobDetail jobDetail : logAnalyzeJobDetailList) {
                //jobDetail,指定和kakfa输入过来的数据中的 requesturl比较
                // 获取kafka输入过来的数据的requesturl的值
                //requesturl:http://www.jd.com/?cu=true&utm_source=p.yiqifa.com&utm_medium=tuiguang&utm_campaign=t_36378_150_7125194&utm_term=2df8a572a857470aa96d1b47791a71c7
                //job 判断条件中， field  compare value
                //                 requestUrl        包含         value:http://www.jd.com
                String fieldValueInLog = logMessage.getCompareFieldValue(jobDetail.getField());
                //1:包含 2:等于 3：正则
                if (jobDetail.getCompare() == 1 && fieldValueInLog.contains(jobDetail.getValue())) {
                    isMatch = true;
                } else if (jobDetail.getCompare() == 2 && fieldValueInLog.equals(jobDetail.getValue())) {
                    isMatch = true;
                } else {
                    isMatch = false;
                }
                if (!isMatch) {
                    break;
                }
            }
            if (isMatch) {
                String pvKey = "log:" + logAnalyzeJob.getJobName() + ":pv:" + DateUtils.getDate();
                String uvKey = "log:" + logAnalyzeJob.getJobName() + ":uv:" + DateUtils.getDate();
                jedis.incr(pvKey);
                jedis.sadd(uvKey, logMessage.getUserName());
            }
        }
    }

    /**
     * 计算单个指标点击的数量
     */
    public static void processViewLog(LogMessage logMessage) {
        if (jobMap == null || jobDetail == null) {
            loadDataModel();
        }
        List<LogAnalyzeJob> analyzeJobList = jobMap.get(LogTypeConstant.VIEW + "");
        for (LogAnalyzeJob logAnalyzeJob : analyzeJobList) {
            boolean isMatch = false;
            List<LogAnalyzeJobDetail> logAnalyzeJobDetailList = jobDetail.get(logAnalyzeJob.getJobId());
            for (LogAnalyzeJobDetail jobDetail : logAnalyzeJobDetailList) {
                String fieldValueInLog = logMessage.getCompareFieldValue(jobDetail.getField());
                //1:包含 2:等于 3：正则
                if (jobDetail.getCompare() == 1 && fieldValueInLog.contains(jobDetail.getValue())) {
                    isMatch = true;
                } else if (jobDetail.getCompare() == 2 && fieldValueInLog.equals(jobDetail.getValue())) {
                    isMatch = true;
                } else {
                    isMatch = false;
                }
                if (!isMatch) {
                    break;
                }
            }
            if (isMatch) {
                String pvKey = "log:" + logAnalyzeJob.getJobName() + ":pv:" + DateUtils.getDate();
                String uvKey = "log:" + logAnalyzeJob.getJobName() + ":uv:" + DateUtils.getDate();
                jedis.incr(pvKey);
                jedis.sadd(uvKey, logMessage.getUserName());
            }
        }
    }

    /**
     * 加载JobDetailMap
     *
     * @return
     */
    private static Map<String, List<LogAnalyzeJobDetail>> loadJobDetailMap() {
        HashMap<String, List<LogAnalyzeJobDetail>> map = new HashMap<String, List<LogAnalyzeJobDetail>>();
        List<LogAnalyzeJobDetail> logAnalyzeJobDetailList = new LogAnalyzeDao().loadJobDetailList();
        for (LogAnalyzeJobDetail logAnalyzeJobDetail : logAnalyzeJobDetailList) {
            int jobId = logAnalyzeJobDetail.getJobId();
            List<LogAnalyzeJobDetail> jobDetails = map.get(jobId + "");
            if (jobDetails == null || jobDetails.size() == 0) {
                jobDetails = new ArrayList<LogAnalyzeJobDetail>();
                map.put(jobId + "", jobDetails);
            }
            jobDetails.add(logAnalyzeJobDetail);
        }
        return map;
    }

    /**
     * 加载JobMap
     *
     * @return
     */
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

    /**
     * 判断jobType否是符合规则，且是有效的
     *
     * @param jobType
     * @return
     */
    public static boolean isValidType(int jobType) {
        if (jobType == LogTypeConstant.BUY || jobType == LogTypeConstant.CLICK || jobType == LogTypeConstant.SEARCH || jobType == LogTypeConstant.VIEW) {
            return true;
        }
        return false;
    }

    /**
     * 加载数据
     */
    public synchronized static void loadDataModel() {
        if (jobMap == null) {
            jobMap = loadJobMap();
        }
        if (jobDetail == null) {
            jobDetail = loadJobDetailMap();
        }
    }

    /**
     * 定时加载配置信息
     * 配合reloadDataModel模块一起使用。
     * 主要实现原理如下：
     * 1，获取分钟的数据值，当分钟数据是10的倍数，就会触发reloadDataModel方法，简称reload时间。
     * 2，reloadDataModel方式是线程安全的，在当前worker中只有一个现成能够操作。
     * 3，为了保证当前线程操作完毕之后，其他线程不再重复操作，设置了一个标识符reloaded。
     * 在非reload时间段时，reloaded一直被置为true；
     * 在reload时间段时，第一个线程进入reloadDataModel后，加载完毕之后会将reloaded置为false。
     */
    public static void scheduleLoad() {
        String dateTime = DateUtils.getDateTime();
        int now = Integer.parseInt(dateTime.split(":")[1]);
        if (now % 10 == 0) {
            reloadDataModel();
        } else {
            reloaded = true;
        }
    }

    /**
     * 配置scheduleLoad重新加载底层数据模型。
     */
    private synchronized static void reloadDataModel() {
        if (reloaded) {
            jobMap = loadJobMap();
            jobDetail = loadJobDetailMap();
            reloaded = false;
        }
    }

}
