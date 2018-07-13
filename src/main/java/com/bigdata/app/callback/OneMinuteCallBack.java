package com.bigdata.app.callback;

import com.bigdata.app.dao.CacheData;
import com.bigdata.app.domian.BaseRecord;
import com.bigdata.storm.dao.LogAnalyzeDao;
import com.bigdata.storm.domain.LogAnalyzeJob;
import com.bigdata.storm.util.DateUtils;
import com.bigdata.storm.util.MySharedJedisPool;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.ShardedJedis;

import java.util.*;

/**
 * Created with IDEA by User1071324110@qq.com
 * 计算每分钟的增量信息
 * @author 10713
 * @date 2018/7/13 17:11
 */
public class OneMinuteCallBack implements Runnable {

    public void run() {
        Calendar calendar = Calendar.getInstance();
        //24:00分时，将缓存清空
        if (calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.HOUR) == 24) {
            CacheData.setPvMap(new HashMap<String, Integer>());
            CacheData.setUvMap(new HashMap<String, Long>());
        }
        //主要逻辑
        String date = DateUtils.getDate();
        //从redis中获取所有的指标最新的值
        List<BaseRecord> baseRecordList = getBaseRecords(date);
        //计算增量数据： 用最新全量值减去上一个时间段的全量值 上一个时间段的值，存放在cacheData
        List<BaseRecord> appendDataList = getAppData(baseRecordList);
        //将增量数据保存到mysql中
        new LogAnalyzeDao().saveMinuteAppendRecord(appendDataList);


    }

    /**
     *
     * @param baseRecordList 包含全值的list，
     * @return 包含差值的list
     */
    private List<BaseRecord> getAppData(List<BaseRecord> baseRecordList) {
        List<BaseRecord> appendDataList = new ArrayList<BaseRecord>();
        for (BaseRecord baseRecord : baseRecordList) {
            //用最新的pv减去缓存中的pv值，得到最新的增量数据
            //在CacheData中 用map保存上一次的全值
            int pvAppendValue = CacheData.getPV(baseRecord.getPv(), baseRecord.getIndexName());
            //用最新的pv减去缓存中的pv值，得到最新的增量数据
            long uvAppendValue = CacheData.getUV(baseRecord.getUv(), baseRecord.getIndexName());
            appendDataList.add(new BaseRecord(baseRecord.getIndexName(), pvAppendValue, uvAppendValue, baseRecord.getProcessTime()));
        }
        return appendDataList;
    }

    /**
     * 从redis中获取每个任务的最新的值（全值） 放入到list中，并返回。
     * @param date 要获取redis中的数据的日期
     * @return BaseRecord的列表 list中的pv、uv是全值
     */
    private List<BaseRecord> getBaseRecords(String date) {
        //1、获取所有的job信息
        List<LogAnalyzeJob> logAnalyzeJobList = new LogAnalyzeDao().loadJobList();
        //2、初始化redis的连接
        ShardedJedis jedis = MySharedJedisPool.getShardedJedisPool().getResource();
        //3、拼装rediskey
        List<BaseRecord> baseRecords = new ArrayList<BaseRecord>();
        for (LogAnalyzeJob logAnalyzeJob : logAnalyzeJobList) {
            //拼装
            String pvKey = "log:" + logAnalyzeJob.getJobName() + ":pv:" + DateUtils.getDate();
            String uvKey = "log:" + logAnalyzeJob.getJobName() + ":uv:" + DateUtils.getDate();
            //4、获取redis中最新值
            String pv = jedis.get(pvKey);
            if (StringUtils.isBlank(pv)) {
                pv = "0";
            }
            Long uv = jedis.scard(uvKey);
            //5、将当前最新的值，封装到一个对象中，然后将对象存放list中
            BaseRecord baseRecord = new BaseRecord(logAnalyzeJob.getJobName(), Integer.parseInt(pv.trim()), uv, new Date());
            baseRecords.add(baseRecord);
        }
        return baseRecords;
    }
}
