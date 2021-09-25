package cn.fancychuan.process;

import cn.fancychuan.SensorReading;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * 使用侧输出流
 *  1.定义一个OutputTag，给定一个 名称
 *  2.使用 ctx.output(outputTag对象,放入侧输出流的数据)
 *  3.获取侧输出流 => DataStream.getSideOutput(outputTag对象)
 */
public class SideOutputProcessFunction extends KeyedProcessFunction<String, SensorReading, SensorReading> {
    OutputTag<String> alarmTag = new OutputTag<String>("temperatureAlarm"){};

    @Override
    public void processElement(SensorReading value, KeyedProcessFunction<String, SensorReading, SensorReading>.Context ctx, Collector<SensorReading> out) throws Exception {
        if (value.getTemperature() > 30) {
            ctx.output(alarmTag, "温度高于30！");
        }
        out.collect(value);
    }
}
