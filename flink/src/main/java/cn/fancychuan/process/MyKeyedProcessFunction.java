package cn.fancychuan.process;

import cn.fancychuan.SensorReading;
import org.apache.flink.streaming.api.TimerService;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;

public class MyKeyedProcessFunction extends KeyedProcessFunction<String, SensorReading, Long> {
    /**
     * 到了定时器设定的时间，所要执行的方法
     * @param timestamp 注册的定时器的时间
     * @param ctx 上下文
     * @param out 采集器
     * @throws Exception
     */
    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<Long> out) throws Exception {
        System.out.println(new Timestamp(timestamp) + "_触发");
    }

    /**
     * 分区中每来一条数据就会调用一次该方法
     */
    @Override
    public void processElement(SensorReading value, Context ctx, Collector<Long> out) throws Exception {
        // 当前数据的分组key
        String currentKey = ctx.getCurrentKey();
        // 当前数据代表的时间戳：根据时间语义，这个时间要么是事件时间，也可以是process time
        ctx.timestamp();
        // 可以将数据放入侧输出流。侧输出流在这里配置可以更灵活一点，不受SensorReading value这种类型的限制
        // ctx.output(outputTag, some);

        // 定时器：注册、删除、当前时间、当前Watermark
        TimerService timerService = ctx.timerService();
        timerService.registerProcessingTimeTimer(
                // 设置的“闹钟”为：当前处理时间往后5秒
                timerService.currentProcessingTime() + 5000L
        );
        // timerService.registerEventTimeTimer();
        // timerService.deleteEventTimeTimer();
        // timerService.deleteProcessingTimeTimer();
        // timerService.currentProcessingTime();
        // timerService.currentWatermark();

    }
}
