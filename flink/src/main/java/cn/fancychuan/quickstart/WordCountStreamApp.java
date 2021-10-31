package cn.fancychuan.quickstart;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WordCountStreamApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> dataStream = env.socketTextStream("hadoop101", 7777);
        SingleOutputStreamOperator<Tuple2<String, Integer>> resultStream = dataStream
                .flatMap(new FlatMapFunction<String, String>() {
                    @Override
                    public void flatMap(String s, Collector<String> collector) throws Exception {
                        String[] items = s.split(" ");
                        for (String item : items) {
                            collector.collect(item);
                        }
                    }
                }).map(new MapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(String s) throws Exception {
                        return Tuple2.of(s, 1);
                    }
                })
                .keyBy(tuple2 -> tuple2.f0)
                .sum(1);
        resultStream.print("wordcount");

        env.execute();
    }
}
