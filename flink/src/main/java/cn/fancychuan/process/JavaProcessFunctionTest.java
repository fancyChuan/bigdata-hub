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
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.util.OutputTag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;

public class JavaProcessFunctionTest {
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

    /**
     * 练习需求：监控温度传感器，如果温度值在5s之内(processing time)连续上升，则报警。
     *
     * sensor_1,1547719200,15.8     # 第一条数据，设置了定时器
     * sensor_1,1547719203,16.8
     * sensor_1,1547719204,17.8
     * sensor_1,1547719205,18.8     # 告警
     * sensor_1,1547719206,10.8
     * sensor_1,1547719207,9.8      # 删除了206的定时器，重新注册了207的定时器，因此下一个告警时刻是212
     * sensor_1,1547719208,12.8
     * sensor_1,1547719211,17.8
     * sensor_1,1547719212,20.8     # 告警
     * sensor_1,1547719213,22.8     # 不会告警，因为只有一个告警器（不考虑持续报警）
     * sensor_3,1547719214,12.8     # 插入了一条sensor_id不同的数据，这个时候会注册一个sensor_3的定时器
     * sensor_1,1547719219,22.8     # 虽然传的sensor_1，但还是告警了，说明在keyedProcessFunction中使用全局变量，不同id的数据都会访问到
     */
    @Test
    public void testTempDown() {
        DataStream<SensorReading> dataStream = getDataStream();
        // 使用AssignerWithPunctuatedWatermarks避免使用AscendingTimestampExtractor时"时间戳+1"的影响
        dataStream = dataStream.assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks<SensorReading>() {
            private Long maxTs = Long.MIN_VALUE;
            @Nullable
            @Override
            public Watermark checkAndGetNextWatermark(SensorReading lastElement, long extractedTimestamp) {
                this.maxTs = Math.max(this.maxTs, extractedTimestamp);
                return new Watermark(this.maxTs);
            }

            @Override
            public long extractTimestamp(SensorReading element, long recordTimestamp) {
                return element.getTimestamp() * 1000;
            }
        });

        SingleOutputStreamOperator<String> processStream = dataStream.keyBy(SensorReading::getId)
                .process(new TempDownKeyedProcesssFunc());
        processStream.print("tempdown");
    }

    /**
     * 使用状态实现需求：监控温度传感器，如果温度值在5s之内(processing time)连续上升，则报警。
     * 演示状态隔离
     * sensor_1,1547719200,15.8     # 第一条数据，设置了定时器为205，此时定时器内的数据id是sensor_1
     * sensor_1,1547719203,16.8
     * sensor_2,1547719204,17.8     # 这里也设置一个定时器为209
     * sensor_2,1547719205,18.8     # 触发告警. 虽然是sensor_2，但是定时器是跟Watermark有关，与id无关，
     *                              # 因此这条数据还是触发了第一条数据设置的定时器，查出来的温度是第2条的16.8而不是18.8
     * sensor_1,1547719209,17.9     # 虽然温度17.9比18.8小，但是因为状态隔离，所以比较的是第3条数据的17.8
     *                              # 触发的是第4条数据sensor_2所设置的定时器209，而保存的温度也是第4条的18.8，而不是这条数据的17.9
     */
    @Test
    public void testTempDownState() {
        DataStream<SensorReading> dataStream = getDataStream();
        // 使用AssignerWithPunctuatedWatermarks避免使用AscendingTimestampExtractor时"时间戳+1"的影响
        dataStream = dataStream.assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks<SensorReading>() {
            private Long maxTs = Long.MIN_VALUE;
            @Nullable
            @Override
            public Watermark checkAndGetNextWatermark(SensorReading lastElement, long extractedTimestamp) {
                this.maxTs = Math.max(this.maxTs, extractedTimestamp);
                return new Watermark(this.maxTs);
            }

            @Override
            public long extractTimestamp(SensorReading element, long recordTimestamp) {
                return element.getTimestamp() * 1000;
            }
        });

        SingleOutputStreamOperator<String> processStream = dataStream.keyBy(SensorReading::getId)
                .process(new TempDownStateKeyedProcesssFunc());
        processStream.print("tempdown-state");
    }

    @Test
    public void testSideOutput() {
        DataStream<SensorReading> dataStream = getDataStream();
        SingleOutputStreamOperator<SensorReading> processStream = dataStream.keyBy(SensorReading::getId).process(new SideOutputProcessFunction());

        OutputTag<String> alarmTag = new OutputTag<String>("temperatureAlarm") {};
        processStream.getSideOutput(alarmTag).print("alarm");
        processStream.print();
    }
}

