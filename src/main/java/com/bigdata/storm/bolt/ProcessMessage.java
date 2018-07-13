package com.bigdata.storm.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import com.bigdata.storm.domain.LogMessage;

import javax.sound.midi.Soundbank;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/13 11:54
 */
public class ProcessMessage extends BaseBasicBolt {

    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        LogMessage message = (LogMessage) tuple.getValueByField("message");
        Integer type = tuple.getIntegerByField("type");
        System.out.println("=========================================");
        System.out.println("message :" + message);
        System.out.println("type:" + type);
        System.out.println("=========================================");
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
