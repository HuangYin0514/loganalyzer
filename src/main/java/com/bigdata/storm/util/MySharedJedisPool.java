package com.bigdata.storm.util;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.LinkedList;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 21:34
 */
public class MySharedJedisPool {

    private static ShardedJedisPool shardedJedisPool;

    static{
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(5);
        config.setMaxTotal(-1);
        config.setMaxWaitMillis(5);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        LinkedList<JedisShardInfo> list = new LinkedList<JedisShardInfo>();
        list.add(new JedisShardInfo("192.168.25.181", 6379));
        shardedJedisPool = new ShardedJedisPool(config, list);
    }

    public static ShardedJedisPool getShardedJedisPool() {
        return shardedJedisPool;
    }
}
