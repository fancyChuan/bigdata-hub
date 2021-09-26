package cn.fancychuan.process;

import cn.fancychuan.SensorReading;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * 使用状态来实现需求：传感器温度在5s之内(event time)连续上升，则报警。
 *  {@link TempDownKeyedProcesssFunc} 的“分组状态隔离”改进版
 */
public class TempDownStateKeyedProcesssFunc extends KeyedProcessFunction<String, SensorReading, String> {
    /**
     * 使用两个状态对象
     */
    private ValueState<Double> lastTemp;
    private ValueState<Long> alarmTime;

    @Override
    public void open(Configuration parameters) throws Exception {
        lastTemp = getRuntimeContext().getState(new ValueStateDescriptor<Double>("lastTempState", Double.class, 0.0));
        alarmTime = getRuntimeContext().getState(new ValueStateDescriptor<Long>("alarmTimeState", Long.class, 0L));
    }

    @Override
    public void processElement(SensorReading value, KeyedProcessFunction<String, SensorReading, String>.Context ctx, Collector<String> out) throws Exception {
        // 判断是上升还是下降
        // 如果下降，则新注册一个定时器（之前的定时器删除）；如果上升，则继续监测下一条流
        // 下降的情况，或者第一条数据来的时候
        if (value.getTemperature() < lastTemp.value() || alarmTime.value() == 0L) {
            if (alarmTime.value() > 0) {
                //
                ctx.timerService().deleteEventTimeTimer(alarmTime.value());
            }
            alarmTime.update(value.getTimestamp() * 1000L + 5000L);
            ctx.timerService().registerEventTimeTimer(alarmTime.value());
        }
        lastTemp.update(value.getTemperature());

    }

    @Override
    public void onTimer(long timestamp, KeyedProcessFunction<String, SensorReading, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
        // 此处的ctx.getCurrentKey()行为是：注册的时候，
        out.collect(ctx.getCurrentKey() + "在" + ctx.timestamp() + "监测到温度连续5s上升" +
                "，当前水位为: " + ctx.timerService().currentWatermark() +
                "，当前保存的温度为: " + lastTemp.value());
    }
}
