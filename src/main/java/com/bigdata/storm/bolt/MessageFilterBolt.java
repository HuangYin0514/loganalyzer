package com.bigdata.storm.bolt;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.bigdata.storm.domain.LogMessage;
import com.bigdata.storm.util.LogAnalyzeHandler;

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
        //2、对数据进行解析
        LogMessage logMessage = LogAnalyzeHandler.parser(line);
        //3、对数据类型进行过滤
        if (logMessage == null || !LogAnalyzeHandler.isValidType(logMessage.getType())) {
            return;
        }
        //4、如果满足条件，发送给下游做处理
        collector.emit(new Values(logMessage.getType(), logMessage));
        //异步：定时更新规则信息
        LogAnalyzeHandler.scheduleLoad();
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //根据点击内容类型将日志进行区分
        declarer.declare(new Fields("type", "message"));
    }
}
