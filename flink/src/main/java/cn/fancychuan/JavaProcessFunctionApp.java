package cn.fancychuan;

import cn.fancychuan.process.MyKeyedProcessFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JavaProcessFunctionApp {
    private StreamExecutionEnvironment env;

    @Before
    public void before() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 设置时间语义
//        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
    }

    @After
    public void after() throws Exception {
        env.execute();
    }
    
    @Test    
    public void testKeyedProcessFunction() {
        DataStreamSource<String> inputStream = env.socketTextStream("hadoop101", 7777);
        SingleOutputStreamOperator<SensorReading> dataStream = inputStream.map(new MapFunction<String, SensorReading>() {
            @Override
            public SensorReading map(String s) throws Exception {
                String[] items = s.split(",");
                return new SensorReading(items[0], Long.parseLong(items[1]), new Double(items[2]));
            }
        });

        KeyedStream<SensorReading, String> keyedStream = dataStream.keyBy(SensorReading::getId);

        SingleOutputStreamOperator<Long> processResult = keyedStream.process(new MyKeyedProcessFunction());

    }
}

