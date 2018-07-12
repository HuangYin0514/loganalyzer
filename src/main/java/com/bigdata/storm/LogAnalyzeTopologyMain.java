package com.bigdata.storm;

import backtype.storm.topology.TopologyBuilder;
import com.bigdata.storm.spout.RandomSpout;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 20:56
 */
public class LogAnalyzeTopologyMain {

    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("kafka-spout", new RandomSpout(), 3);

    }
}
