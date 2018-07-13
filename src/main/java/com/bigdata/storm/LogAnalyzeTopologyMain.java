package com.bigdata.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import com.bigdata.storm.bolt.MessageFilterBolt;
import com.bigdata.storm.bolt.ProcessMessage;
import com.bigdata.storm.spout.RandomSpout;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 20:56
 */
public class LogAnalyzeTopologyMain {

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("kafka-spout", new RandomSpout(), 3);
        builder.setBolt("messageFilter-bolt", new MessageFilterBolt(), 3).shuffleGrouping("kafka-spout");
        builder.setBolt("ProcessMessage-bolt", new ProcessMessage(), 3)
                .fieldsGrouping("messageFilter-bolt", new Fields("type"));

        Config config = new Config();
        if (args != null && args.length > 0) {
            config.setNumWorkers(2);
            StormSubmitter.submitTopologyWithProgressBar(args[0], config, builder.createTopology());
        } else {
            config.setMaxTaskParallelism(3);
            config.setDebug(true);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("LogAnalyzeTopologyMain", config, builder.createTopology());
            Utils.sleep(100000);
            cluster.shutdown();
        }

    }
}
