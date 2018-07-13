package com.bigdata.storm.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import com.bigdata.storm.domain.LogMessage;
import com.bigdata.storm.util.LogAnalyzeHandler;

import javax.sound.midi.Soundbank;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/13 11:54
 */
public class ProcessMessage extends BaseBasicBolt {

    /**
     * 如果message符合之前输入的分析日志规则，那么存储到redis中。
     * 在redis中
     *      pv 用String类型
     *      uv 用set类型  插入方法为sadd
     *
     * @param tuple
     * @param basicOutputCollector
     */
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        LogMessage message = (LogMessage) tuple.getValueByField("message");
        LogAnalyzeHandler.process(message);
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
