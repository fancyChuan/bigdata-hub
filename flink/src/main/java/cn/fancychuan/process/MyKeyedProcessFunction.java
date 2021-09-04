package cn.fancychuan.process;

import cn.fancychuan.SensorReading;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class MyKeyedProcessFunction extends KeyedProcessFunction<String, SensorReading, Long> {
    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<Long> out) throws Exception {
        super.onTimer(timestamp, ctx, out);
    }

    @Override
    public void processElement(SensorReading value, Context ctx, Collector<Long> out) throws Exception {

    }
}
