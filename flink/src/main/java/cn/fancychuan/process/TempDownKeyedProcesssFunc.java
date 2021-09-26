package cn.fancychuan.process;

import cn.fancychuan.SensorReading;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * 需求：传感器温度在5s之内(event time)连续上升，则报警。
 */
public class TempDownKeyedProcesssFunc extends KeyedProcessFunction<String, SensorReading, String> {
    /**
     * 这里的案例是处于演示的作用，在输入的时候只出入了一个sensor_id的数据，一旦出现多个sensor_id的时候，这个程序是有问题的
     * 因为processElement是流的所有数据都会访问到，不管是不是同一个sensor_id
     * 换句话说，不是“分组隔离的”
     * 隔离的版本，查看 {@link TempDownStateKeyedProcesssFunc}
     */
    // 保留上一个流的温度
    private Double lastTemp = 0.0;
    // 定时器触发的时间
    private Long alarmTime = 0L;

    @Override
    public void processElement(SensorReading value, KeyedProcessFunction<String, SensorReading, String>.Context ctx, Collector<String> out) throws Exception {
        // 判断是上升还是下降
        // 如果下降，则新注册一个定时器（之前的定时器删除）；如果上升，则继续监测下一条流
        // 下降的情况，或者第一条数据来的时候
        if (value.getTemperature() < lastTemp || alarmTime == 0L) {
            if (alarmTime > 0) {
                //
                ctx.timerService().deleteEventTimeTimer(alarmTime);
            }
            alarmTime = value.getTimestamp() * 1000L + 5000L;
            ctx.timerService().registerEventTimeTimer(alarmTime);
        }
        lastTemp = value.getTemperature();

    }

    @Override
    public void onTimer(long timestamp, KeyedProcessFunction<String, SensorReading, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
        // 此处的ctx.getCurrentKey()行为是：注册的时候，
        out.collect(ctx.getCurrentKey() + "在" + ctx.timestamp() + "监测到水位连续5s上升");
    }
}
