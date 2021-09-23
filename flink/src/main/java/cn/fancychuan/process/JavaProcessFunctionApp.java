package cn.fancychuan.process;

import cn.fancychuan.SensorReading;
import cn.fancychuan.process.MyKeyedProcessFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
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

    /**
     * 测试keyedProcessFunction总ctx.timestamp()的不同行为
     * 1. 时间语义：process time，不指定时间提取器
     *      ctx.timestamp()==null
     * 2. 时间语义：process time，指定时间提取器
     *      ctx.timestamp() == 指定的流中的
     * 3. 时间语义：event time，不指定时间提取器
     *      ctx.timestamp()==null
     * 4. 时间语义：event time，指定时间提取器
     *      ctx.timestamp() == 指定的流中的时间戳
     *  也就是说，ctx.timestamp()是否为空与是否指定了提取器有关，具体的数值与时间语义相关
     */
    @Test    
    public void testKeyedProcessFunction() {
        DataStream<SensorReading> dataStream = getDataStream();
        // assignTimestampsAndWatermarks影响KeyedProcessFunction中的ctx.timestamp()是否有值
        dataStream = dataStream.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<SensorReading>() {
            @Override
            public long extractAscendingTimestamp(SensorReading element) {
                return element.getTimestamp() * 1000;
            }
        });
        KeyedStream<SensorReading, String> keyedStream = dataStream.keyBy(SensorReading::getId);

        SingleOutputStreamOperator<Long> processResult = keyedStream.process(new MyKeyedProcessFunction());

    }
}

