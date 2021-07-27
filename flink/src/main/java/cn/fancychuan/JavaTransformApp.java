package cn.fancychuan;

import cn.fancychuan.SensorReading;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;


/**
 * flink转换算子测试：
 * 1.max()和maxBy()的区别：
 *  max(x)的结果，其他字段不变，只针对x取所有流的最大值
 *  maxBy(x)的结果，其他字段会取最大的x所对应的那个流
 */
public class JavaTransformApp {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> inputStream = env.readTextFile("E:\\JavaWorkshop\\bigdata-learn\\flink\\src\\main\\resources\\sensor.txt");

        // 1. map算子
        DataStream<Integer> mapStream = inputStream.map(new MapFunction<String, Integer>() {
            @Override
            public Integer map(String s) throws Exception {
                return s.length();
            }
        });
        // 2. flatMap算子
        DataStream<String> flatMapStream = inputStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                String[] items = s.split(",");
                for (String item : items) {
                    collector.collect(item);
                }
            }
        });
        // 3. filter
        DataStream<String> filterStream = inputStream.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String s) throws Exception {
                if (s.startsWith("sensor_10")) {
                    return true;
                }
                return false;
            }
        });

        mapStream.print("map");
        flatMapStream.print("flatMap");
        filterStream.print("filter");

        System.out.println("==================");
        // 4.keyBy
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] items = line.split(",");
            return new SensorReading(items[0], Long.parseLong(items[1]), new Double(items[2]));
        });
        KeyedStream<SensorReading, Tuple> keyedStream = dataStream.keyBy("id");
        // 另一种写法： KeyedStream<SensorReading, String> keyedStream1 = dataStream.keyBy(data -> data.getId());
        KeyedStream<SensorReading, String> keyedStream1 = dataStream.keyBy(SensorReading::getId);
        SingleOutputStreamOperator<SensorReading> maxTemperature = keyedStream.max("temperature");
        SingleOutputStreamOperator<SensorReading> maxByTemperature = keyedStream.maxBy("temperature");
        maxTemperature.print("keyBy-max");
        maxByTemperature.print("keyBy-maxBy");

        // 5.reduce，实现取最大温度所对应的那组SensorReading数据
        DataStream<SensorReading> resultStream = keyedStream.reduce(new ReduceFunction<SensorReading>() {
            @Override
            public SensorReading reduce(SensorReading value1, SensorReading value2) throws Exception {
                return new SensorReading(value1.getId(), value2.getTimestamp(), Math.max(value1.getTemperature(), value2.getTemperature()));
            }
        });
        resultStream.print("reduce");

        env.execute();
    }
}
