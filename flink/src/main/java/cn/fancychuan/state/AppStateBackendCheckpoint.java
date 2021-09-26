package cn.fancychuan.state;

import cn.fancychuan.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.util.Collector;

/**
 * 状态后端的使用
 * checkpoint的使用和配置
 */
public class AppStateBackendCheckpoint {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        // 设置状态后端
        StateBackend memoryStateBackend = new MemoryStateBackend();
        env.setStateBackend(memoryStateBackend);
        // 使用FsStateBackend
        StateBackend fsStateBackend = new FsStateBackend("hdfs://hadoop101:8020/forlearn/flink/statebackend");
        env.setStateBackend(fsStateBackend);
        // 使用RocksDBStateBackend，这种企业较为常见
        StateBackend rocksDBStateBackend = new RocksDBStateBackend("hdfs://hadoop101:8020/forlearn/flink/statebackend/rocksdb");
        env.setStateBackend(rocksDBStateBackend);
        // 开启checkpoint
        env.enableCheckpointing(3000L);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE); // 设置checkpoint的一致性级别
        env.getCheckpointConfig().setCheckpointTimeout(30000L);     // ck执行多久超时
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);   // 设置异步有多少个ck在同时进行
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500L);  // 上一个ck结束，到下一个ck开始，最小间隔
        env.getCheckpointConfig().setPreferCheckpointForRecovery(false);    // 默认为false，表示从checkpoint恢复。true：表示从savepoint恢复
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(3);   // 允许当前checkpoint的次数

        DataStreamSource<String> inputStream = env.socketTextStream("hadoop101", 7777);
        SingleOutputStreamOperator<SensorReading> dataStream = inputStream.map(new MapFunction<String, SensorReading>() {
            @Override
            public SensorReading map(String s) throws Exception {
                String[] items = s.split(",");
                return new SensorReading(items[0], Long.parseLong(items[1]), new Double(items[2]));
            }
        });

        env.execute();
    }
}
