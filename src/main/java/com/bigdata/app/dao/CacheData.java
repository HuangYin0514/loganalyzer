package com.bigdata.app.dao;

import lombok.Data;
import sun.misc.VM;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IDEA by User1071324110@qq.com
 * <p>
 * redis中保存的是全量数据， MySQL中保存的是增量数据
 * Describe: 缓存上一分钟的数据，用来做cache
 * 整点的时候，cache层中的数据，需要清理掉
 *
 * @author 10713
 * @date 2018/7/13 16:56
 */
@Data
public class CacheData {
    //Map<String, Integer>  redisKey->上一分钟的全量值为 value
    private static Map<String, Integer> pvMap = new HashMap<String, Integer>();
    private static Map<String, Long> uvMap = new HashMap<String, Long>();

    /**
     * @param pv        最新的全值
     * @param indexName redis中的全值
     * @return 增量
     */
    public static int getPV(int pv, String indexName) {
        Integer cacheValue = pvMap.get(indexName);
        //什么样的情况下，缓存缓存中的值为null，
        //1、程序刚启动的
        //2、零点
        if (cacheValue == null) {
            cacheValue = 0;
            pvMap.put(indexName, cacheValue);
        }
        //伪代码：
        // 如果上一分钟的cachevalue ==0 ,并且当前时间不等于24：00
        // 连接数据库，将从0点到上一分钟的所有增量数据相
        //      数据库中保存的是增量数据
        // 将相加的结果作为cachevalue

        //计算增量值
        //正常情况下，当前最新的pv会大于等于上一分钟的
        //1、零点转钟
        //2、或者一分钟pv值没有进行更新
        if (pv > cacheValue.intValue()) {
            pvMap.put(indexName, pv);
            return pv - cacheValue.intValue();//将新的值赋值给cachaData
        }
        return 0;//如果新的值小于旧的值，直接返回0
    }

    /**
     * @param uv        最新的全值
     * @param indexName redis中的全值
     * @return 增量
     */
    public static long getUV(long uv, String indexName) {
        Long cacheValue = uvMap.get(indexName);
        if (cacheValue == null) {
            cacheValue = 0L;
            uvMap.put(indexName, cacheValue);
        }
        if (uv > cacheValue.longValue()) {
            uvMap.put(indexName, uv);
            return uv - cacheValue;
        }
        return 0;
    }

    public static Map<String, Integer> getPvMap() {
        return pvMap;
    }

    public static void setPvMap(Map<String, Integer> pvMap) {
        CacheData.pvMap = pvMap;
    }

    public static Map<String, Long> getUvMap() {
        return uvMap;
    }

    public static void setUvMap(Map<String, Long> uvMap) {
        CacheData.uvMap = uvMap;
    }
}
