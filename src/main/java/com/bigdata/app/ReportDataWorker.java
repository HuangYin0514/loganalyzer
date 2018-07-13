package com.bigdata.app;

import com.bigdata.app.callback.OneMinuteCallBack;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IDEA by User1071324110@qq.com
 * 计算每个指标每分钟的增量数据
 *
 * @author 10713
 * @date 2018/7/13 17:08
 */
public class ReportDataWorker {
    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        //计算每分钟的增量数据，并将增量数据保存到mysql数据库中
        scheduledExecutorService.scheduleAtFixedRate(new OneMinuteCallBack(), 0, 60, TimeUnit.SECONDS);
        //计算每半个小时的增量数据，并将增量数据保存到mysql数据库中
        //scheduledExecutorService.scheduleAtFixedRate(new HalfAppendCallBack(), 0, 60, TimeUnit.SECONDS);
        //计算每小时的增量数据，并将增量数据保存到mysql数据库中
        //scheduledExecutorService.scheduleAtFixedRate(new HourAppendCallBack(), 0, 60, TimeUnit.SECONDS);
        //计算每天的全量，并将增量数据保存到mysql数据库中
        //scheduledExecutorService.scheduleAtFixedRate(new DayAppendCallBack(), 0, 60, TimeUnit.SECONDS);
    }
}
