package cn.fancychuan;

import cn.fancychuan.SensorReading;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.util.Collector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.crypto.Data;
import java.util.Collections;


/**
 * flink转换算子测试：
 *  max()和maxBy()的区别：
 *      - max(x)的结果，其他字段不变，只针对x取所有流的最大值
 *      - maxBy(x)的结果，字段x和其他字段都会取最大的x所对应的那个流
 * 1. map
 * 2. flatMap
 * 3. filter
 * 4. keyBy
 */
public class JavaTransformApp {
    private static StreamExecutionEnvironment env;

    @Before
    public void before() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
    }
    @After
    public void after() throws Exception {
        env.execute("sourceApp");
    }

    public static DataStream<String> getInputStream() {
        DataStream<String> inputStream = env.readTextFile("E:\\JavaWorkshop\\bigdata-learn\\flink\\src\\main\\resources\\sensor.txt");
        inputStream.print("inputStream");
        return inputStream;
    }
    public static DataStream<SensorReading> getDataStream() {
        DataStream<String> inputStream = getInputStream();
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] items = line.split(",");
            return new SensorReading(items[0], Long.parseLong(items[1]), new Double(items[2]));
        });
        return dataStream;
    }

    @Test
    public void testMap() {
        // 1. map算子
        DataStream<String> inputStream = getInputStream();
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
    }

    @Test
    public void testKeyByAndReduce() {
        // 4.keyBy
        DataStream<SensorReading> dataStream = getDataStream();
        KeyedStream<SensorReading, Tuple> keyedStream = dataStream.keyBy("id");
        // 另一种写法： KeyedStream<SensorReading, String> keyedStream1 = dataStream.keyBy(data -> data.getId());
        KeyedStream<SensorReading, String> keyedStream1 = dataStream.keyBy(SensorReading::getId);
        // - max(x)的结果，其他字段不变，只针对x取所有流的最大值
        // - maxBy(x)的结果，字段x和其他字段都会取最大的x所对应的那个流
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
    }

    @Test
    public void testSplitAndConnect() {
        // 6. 分流 split 和 select
        DataStream<SensorReading> dataStream = getDataStream();
        SplitStream<SensorReading> splitStream = dataStream.split(new OutputSelector<SensorReading>() {
            @Override
            public Iterable<String> select(SensorReading sensorReading) {
                return (sensorReading.getTemperature() > 30) ? Collections.singletonList("high") : Collections.singletonList("low");
            }
        });
        DataStream<SensorReading> highStream = splitStream.select("high");
        DataStream<SensorReading> lowStream = splitStream.select("low");
        DataStream<SensorReading> allStream = splitStream.select("high", "low");
//        highStream.print("high");
//        allStream.print("all");

        // 8.Connect 和 CoMap
        // 先将高温流转为二元组类型的流，再与低温流合并
        DataStream<Tuple2<String, Double>> highTupleStream = highStream.map(new MapFunction<SensorReading, Tuple2<String, Double>>() {
            @Override
            public Tuple2<String, Double> map(SensorReading sensorReading) throws Exception {
                return new Tuple2<>(sensorReading.getId(), sensorReading.getTemperature());
            }
        });
        // connectedStreams里面是包含2条流的，虽然在一起了，但是2条流各自独立，就好比“一国两制”
        ConnectedStreams<Tuple2<String, Double>, SensorReading> connectedStreams = highTupleStream.connect(lowStream);
        // 第3个参数表示的是合并map处理后的流的类型，相当于一国两制中的“一国”
        SingleOutputStreamOperator<Object> coMapStream = connectedStreams.map(new CoMapFunction<Tuple2<String, Double>, SensorReading, Object>() {
            @Override
            public Object map1(Tuple2<String, Double> value) throws Exception {
                return new Tuple3<>(value.f0, value.f1, "高温告警！");
            }

            @Override
            public Object map2(SensorReading sensorReading) throws Exception {
                return new Tuple2<>(sensorReading.getId(), "正常~");
            }
        });
        coMapStream.print();

        // 9. union 可以合并多条流，但是要求每条流的类型一样，而connect的2条流类型可以不一样
        DataStream<SensorReading> unionStream = highStream.union(lowStream, allStream);
        unionStream.print("unionStream");
    }
}
