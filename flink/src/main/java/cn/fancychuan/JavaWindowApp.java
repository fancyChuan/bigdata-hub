package cn.fancychuan;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class JavaWindowApp {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> inputStream = env.readTextFile("E:\\JavaWorkshop\\bigdata-learn\\flink\\src\\main\\resources\\sensor.txt");
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] items = line.split(",");
            return new SensorReading(items[0], Long.parseLong(items[1]), new Double(items[2]));
        });

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
}
