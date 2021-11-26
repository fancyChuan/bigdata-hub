package cn.fancychuan.state;

import cn.fancychuan.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.util.Collector;

import javax.annotation.Nullable;

/**
 * 练习：如果连续两次温度的温度差大于20，则告警
 * 分组之间状态隔离
 * sensor_1,1547719200,15.8
 * sensor_1,1547719203,36.8     # 告警
 * sensor_2,1547719204,6.8      # 状态隔离，不会告警
 * sensor_2,1547719205,33.8     # 告警
 * sensor_1,1547719207,15.8     # 告警
 */
public class AppStateTempDiff {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStreamSource<String> inputStream = env.socketTextStream("hadoop101", 7777);
        SingleOutputStreamOperator<SensorReading> dataStream = inputStream.map(new MapFunction<String, SensorReading>() {
            @Override
            public SensorReading map(String s) throws Exception {
                String[] items = s.split(",");
                return new SensorReading(items[0], Long.parseLong(items[1]), new Double(items[2]));
            }
        }).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<SensorReading>() {
            @Override
            public long extractAscendingTimestamp(SensorReading element) {
                return element.getTimestamp() * 1000L;
            }
        });

        dataStream.keyBy(SensorReading::getId)
                .process(new KeyedProcessFunction<String, SensorReading, String>() {
                    private ValueState<Double> lastTemp;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        lastTemp = getRuntimeContext().getState(new ValueStateDescriptor<Double>("lastTemp", Double.class, 0.0));
                    }

                    @Override
                    public void processElement(SensorReading value, KeyedProcessFunction<String, SensorReading, String>.Context ctx, Collector<String> out) throws Exception {
                        if (Math.abs(lastTemp.value() - value.getTemperature()) > 20) {
                            out.collect("在" + ctx.timestamp() + "检测到" + ctx.getCurrentKey() + "温度差超20" + "，当前温度值为：" + lastTemp.value());
                        }
                        lastTemp.update(value.getTemperature());
                    }
                }).print("temp-diff");

        env.execute();
    }
}
