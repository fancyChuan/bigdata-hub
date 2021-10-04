package cn.fancychuan;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JavaWindowApp {
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
    public void testCreateWindow() throws Exception {
        DataStream<SensorReading> dataStream = getDataStream();
        KeyedStream<SensorReading, Tuple> keyedStream = dataStream.keyBy("id");
        // 创建滚动窗口，方式1
        WindowedStream<SensorReading, Tuple, TimeWindow> windowedStream1 = keyedStream.timeWindow(Time.seconds(15));
        // 创建滚动窗口，方式2
        WindowedStream<SensorReading, Tuple, TimeWindow> windowedStream2 = keyedStream.window(TumblingProcessingTimeWindows.of(Time.seconds(15)));
        // 创建滑动窗口，使用的方法跟滚动窗口一样，就是多了一个参数。TODO：为什么当第2个参数offset=0的时候，就变成了滚动窗口，难道不是offset=size才是滚动窗口么？
        keyedStream.window(TumblingProcessingTimeWindows.of(Time.seconds(15), Time.seconds(5)));
        keyedStream.timeWindow(Time.seconds(15), Time.seconds(5));
        // 会话窗口，没有sessionWinodw方法，通过下面的方式创建
        keyedStream.window(ProcessingTimeSessionWindows.withGap(Time.seconds(15)));

        // 滚动计数窗口
        keyedStream.countWindow(5).minBy("temperature"); // 每个id的最近5条数据中最低的温度是多少
        keyedStream.countWindow(5, 2).minBy("temperature"); // 每收到2条相同key的数据就计算一次，每一次的计算窗口范围是近10个元素

    }

    /**
     * 增量聚合函数：aggregate
     * 示例：求近5秒的温度平均值
     */
    public void testAggregate() {
        DataStream<SensorReading> dataStream = getDataStream()
                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<SensorReading>() {
                    @Override
                    public long extractAscendingTimestamp(SensorReading element) {
                        return element.getTimestamp() * 1000;
                    }
                });
        SingleOutputStreamOperator<Double> avgStream = dataStream.keyBy(SensorReading::getId)
                .timeWindow(Time.seconds(5))
                .aggregate(new AggregateFunction<SensorReading, Tuple2<Double, Integer>, Double>() {
                    /**
                     * 创建累加器并初始化
                     */
                    @Override
                    public Tuple2<Double, Integer> createAccumulator() {
                        return Tuple2.of(0.0, 0);
                    }

                    /**
                     * 每来一条数据调用一次
                     */
                    @Override
                    public Tuple2<Double, Integer> add(SensorReading sensorReading, Tuple2<Double, Integer> acc) {
                        return Tuple2.of(acc.f0 + sensorReading.getTemperature(), acc.f1 + 1);
                    }

                    @Override
                    public Double getResult(Tuple2<Double, Integer> acc) {
                        return acc.f0 / acc.f1;
                    }

                    /**
                     * 会话窗口才会触发，合并累加器结果
                     */
                    @Override
                    public Tuple2<Double, Integer> merge(Tuple2<Double, Integer> acc1, Tuple2<Double, Integer> acc2) {
                        return Tuple2.of(acc1.f0 + acc2.f0, acc1.f1 + acc2.f1);
                    }
                });
        avgStream.print("avg");
    }

    /**
     * 全窗口函数：分组-开窗-聚合
     * 测试案例：
     * sensor_1,1547718201,35.8
     * sensor_1,1547718204,36.8
     * sensor_1,1547718205,35.5 # 触发全窗口函数，输出结果2
     * sensor_1,1547718206,35.5
     * sensor_1,1547718208,35.5
     * sensor_1,1547718209,35.5
     * sensor_1,1547718210,35.5 # 输出结果4，包括206-209共4条数据
     */
    @Test
    public void testProcessWindow() {
        DataStream<SensorReading> dataStream = getDataStream()
                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<SensorReading>() {
                    @Override
                    public long extractAscendingTimestamp(SensorReading element) {
                        return element.getTimestamp() * 1000;
                    }
                });
        dataStream.keyBy(SensorReading::getId)
                .timeWindow(Time.seconds(5))
                .process(new ProcessWindowFunction<SensorReading, Long, String, TimeWindow>() {
                    /**
                     * 整个窗口的本组数据，存起来，关窗的时候一次性一起计算
                     */
                    @Override
                    public void process(String s, ProcessWindowFunction<SensorReading, Long, String, TimeWindow>.Context context, Iterable<SensorReading> elements, Collector<Long> out) throws Exception {
                        out.collect(elements.spliterator().estimateSize());
                    }
                })
                .print("processWindow");
    }
}
