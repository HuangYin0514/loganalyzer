package com.bigdata.storm.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.bigdata.storm.domain.LogMessage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 20:58
 */
public class RandomSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private TopologyContext context;
    private List<LogMessage> list;

    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.context = context;
        this.collector = collector;
        list = new ArrayList<LogMessage>();
        list.add(new LogMessage(1,"http://www.itcast.cn/product?id=1002","http://www.itcast.cn/","maoxiangyi"));
        list.add(new LogMessage(1,"http://www.itcast.cn/product?id=1002", "http://www.itcast.cn/","usr"));
        list.add(new LogMessage(1, "http://www.itcast.cn/product?id=1002","http://www.itcast.cn/", "maoxiangyi"));
        LogMessage logMessage = new LogMessage(1,"http://www.itcast.cn/product?id=1003","http://www.baidu.com","maoxiangyi");
        logMessage.setAppName("ie7");
        list.add(logMessage);
    }

    /**
     * 发送消息
     */
    public void nextTuple() {
        final Random random = new Random();
        LogMessage msg = list.get(random.nextInt(4));
        this.collector.emit(new Values(new Gson().toJson(msg)));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("message"));
    }
}
