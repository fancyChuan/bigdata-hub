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
        System.out.println(new Timestamp(timestamp) + "_触发_" + timestamp);
    }

    /**
     * 分区中每来一条数据就会调用一次该方法
     */
    @Override
    public void processElement(SensorReading value, Context ctx, Collector<Long> out) throws Exception {
        // 当前数据的分组key
        System.out.println(ctx.getCurrentKey());
        // 当前数据的时间戳，可以是processtime也可以是eventtime
        // 如果程序的时间语义是process time，那么这个值可能为null（当不使用assignTimestampsAndWatermarks方法指定时间提取器时）
        Long timestamp = ctx.timestamp();
        if (timestamp == null) {
            System.out.println("时间戳为_" + timestamp);
        } else {
            System.out.println("时间戳为_" + new Timestamp(timestamp) + "_" + timestamp);
        }
        // 可以将数据放入侧输出流。侧输出流在这里配置可以更灵活一点，不受SensorReading value这种类型的限制
        // ctx.output(outputTag, some);

        // 定时器：注册、删除、当前时间、当前Watermark
        TimerService timerService = ctx.timerService();
        timerService.registerProcessingTimeTimer(
                // 设置的“闹钟”为：当前process time往后5秒
                timerService.currentProcessingTime() + 5000L
        );
        // 定时器注册的类型 跟配置的时间语义没关系
        // 设置事件时间的定时器
//        timerService.registerEventTimeTimer(
//                value.getTimestamp() * 1000L + 4000L
//        );
        // timerService.deleteEventTimeTimer();
        // timerService.deleteProcessingTimeTimer();
        // timerService.currentProcessingTime();
        // timerService.currentWatermark();

    }
}
