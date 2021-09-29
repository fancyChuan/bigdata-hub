package cn.fancychuan;

import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.util.Collector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;


/**
 * 1.shuffle()
 * 2.global()
 * 3.富函数
 * 4.reduce
 */
public class JavaTransformApp2 {
    private StreamExecutionEnvironment env;

    @Before
    public void before() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 设置时间语义
        // env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
    }

    @After
    public void after() throws Exception {
        env.execute();
    }
    public DataStream<SensorReading> getDataStream() {
        DataStreamSource<String> inputStream = env.socketTextStream("hadoop101", 7777);
        SingleOutputStreamOperator<SensorReading> dataStream = inputStream.map(new MapFunction<String, SensorReading>() {
            @Override
            public SensorReading map(String s) throws Exception {
                String[] items = s.split(",");
                return new SensorReading(items[0], Long.parseLong(items[1]), new Double(items[2]));
            }
        });
        return dataStream;
    }

    @Test
    public void testShuffleAndGlobal() throws Exception {
        env.setParallelism(4);

        DataStream<String> inputStream = env.readTextFile(JavaSourceApp.class.getClassLoader().getResource("sensor.txt").getPath());

        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] items = line.split(",");
            return new SensorReading(items[0], Long.parseLong(items[1]), new Double(items[2]));
        });
        dataStream.print("input");

        // DataStream<Tuple2<String, Integer>> mapStream = dataStream.map(new MyMapFunction());
        // DataStream<Tuple2<String, Integer>> richMapStream = dataStream.map(new MyRichMapFunction());
        // richMapStream.print();

        // shuffle操作
        dataStream.shuffle().print("shuffle");

        dataStream.global().print("global");
    }

    /**
     * reduce的特点：
     * - 输入的类型要一致，输出的类型也要一致
     * - 第一条来的数据，不会进入reduce
     *
     * 测试案例：
     * sensor_1,1547718199,35.8
     * sensor_1,1547718299,36.8
     * sensor_1,1547718399,35.5
     */
    @Test
    public void testReduce() {
        DataStream<SensorReading> dataStream = getDataStream();
        SingleOutputStreamOperator<Tuple2<String, Double>> reduceStream = dataStream.map(new MapFunction<SensorReading, Tuple2<String, Double>>() {
                    @Override
                    public Tuple2<String, Double> map(SensorReading bean) throws Exception {
                        return new Tuple2<String, Double>(bean.getId(), bean.getTemperature());
                    }
                })
                .keyBy(tuple2 -> tuple2.f0)
                .reduce(new ReduceFunction<Tuple2<String, Double>>() {
                    @Override
                    public Tuple2<String, Double> reduce(Tuple2<String, Double> value1, Tuple2<String, Double> value2) throws Exception {
                        System.out.println(value1.toString() + "<->" + value2.toString());
                        return Tuple2.of("aaa", value1.f1 + value2.f1);
                    }
                });
        reduceStream.print("reduce");
    }

    // 实现普通的MapFunction
    private static class MyMapFunction implements MapFunction<SensorReading, Tuple2<String, Integer>> {
        @Override
        public Tuple2<String, Integer> map(SensorReading sensorReading) throws Exception {
            return new Tuple2<>(sensorReading.getId(), sensorReading.getId().length());
        }
    }
    // 实现自定义的富函数类
    private static class MyRichMapFunction extends RichMapFunction<SensorReading, Tuple2<String, Integer>> {

        @Override
        public Tuple2<String, Integer> map(SensorReading sensorReading) throws Exception {
            RuntimeContext runtimeContext = getRuntimeContext();
            // 获取当前子任务的序号
            int indexOfThisSubtask = runtimeContext.getIndexOfThisSubtask();
            return new Tuple2<>(sensorReading.getId(), indexOfThisSubtask);
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            // 初始化工作，一般是定义状态，或者建立数据库连接
            System.out.println("------- open --------");
        }

        @Override
        public void close() throws Exception {
            // 一般是关闭连接和清空状态的收尾操作
            System.out.println("close");
        }
    }
}
