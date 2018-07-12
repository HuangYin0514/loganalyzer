package com.bigdata.storm.bolt;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Tuple;

import java.util.Map;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 21:21
 */
public class MessageFilterBolt extends BaseBasicBolt {


    public void execute(Tuple input, BasicOutputCollector collector) {
        String line = input.getString(0);

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
