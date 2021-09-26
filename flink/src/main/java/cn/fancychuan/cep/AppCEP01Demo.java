package cn.fancychuan.cep;

import cn.fancychuan.SensorReading;
import cn.fancychuan.shopapp.PvAndUvCountApp;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;
import java.util.Map;

/**
 * CEP的一个示例
 */
public class AppCEP01Demo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> inputStream = env.readTextFile(PvAndUvCountApp.class.getClassLoader().getResource("sensor.txt").getPath());
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] items = line.split(",");
            return new SensorReading(items[0], Long.parseLong(items[1]), new Double(items[2]));
        });

        // 1. 定义规则
        // where/or是用来指定一个规则，多个where相当于and的关系
        Pattern<SensorReading, SensorReading> pattern = Pattern.<SensorReading>begin("begin")
                .where(new IterativeCondition<SensorReading>() {
                    @Override
                    public boolean filter(SensorReading sensorReading, Context<SensorReading> context) throws Exception {
                        return sensorReading.getId().equals("sensor_1");
                    }
                }).where(new IterativeCondition<SensorReading>() {
                    @Override
                    public boolean filter(SensorReading sensorReading, Context<SensorReading> context) throws Exception {
                        return sensorReading.getTemperature() >= 30;
                    }
                }).or(new IterativeCondition<SensorReading>() {
                    @Override
                    public boolean filter(SensorReading sensorReading, Context<SensorReading> context) throws Exception {
                        return "sensor_6".equals(sensorReading.getId());
                    }
                });
        // 2. 使用规则
        PatternStream<SensorReading> sensorPattern = CEP.pattern(dataStream, pattern);
        // 3. 取出匹配的结果
        SingleOutputStreamOperator<String> result = sensorPattern.select(new PatternSelectFunction<SensorReading, String>() {
            /**
             * 匹配上的数据会放在一个Map中，key就是定义的事件名，value就是匹配上的数据
             */
            @Override
            public String select(Map<String, List<SensorReading>> map) throws Exception {
                return map.get("begin").toString();
            }
        });

        result.print("cep");

        env.execute();
    }
}
