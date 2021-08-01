package cn.fancychuan;

import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.util.Collector;

import java.util.Collections;


/**
 *
 */
public class JavaTransformApp2 {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);

        DataStream<String> inputStream = env.readTextFile("E:\\JavaWorkshop\\bigdata-learn\\flink\\src\\main\\resources\\sensor.txt");


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

        env.execute();
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
