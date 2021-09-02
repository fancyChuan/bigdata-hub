package cn.fancychuan;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

/**
 * java版本的Flink Source
 *  1. 从集合中获取
 *  2. 从文本中获取
 *  3. 从Kafka中读取
 *  4. 自定义Source（随机产生）
 *      集成SourceFunction，并重写两个方法：
 *          - run()  一般会有个循环，不断的产生数据
 *          - cancel() 调用该方法的时候可以终止run里面的循环
 */
public class JavaSourceApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 1. 从集合中获取
        DataStreamSource<SensorReading> streamSource1 = env.fromCollection(Arrays.asList(
                new SensorReading("sensor_1", 1547718199L, 35.8)
                , new SensorReading("sensor_6", 1547718201L, 15.4)
                , new SensorReading("sensor_7", 1547718202L, 6.7)
                , new SensorReading("sensor_10", 1547718205L, 38.1)));
        streamSource1.print("fromCollection");

        // 2. 从文件中读取
        DataStreamSource<String> streamSource2 = env.readTextFile(JavaSourceApp.class.getClassLoader().getResource("sensor.txt").getPath());

        streamSource2.print("fromFile");

        // 3. 从kafka中读取
        Properties props = new Properties();
        props.put("bootstrap.servers", "hadoop101:9092");
        props.setProperty("group.id", "consumer-group");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("auto.offset.reset", "earliest");

        DataStreamSource<String> streamSource3 = env.addSource(new FlinkKafkaConsumer011<String>("sensor", new SimpleStringSchema(), props));
        streamSource3.print("fromKafka");

        // 4. 自定义数据源（随机产生）
        SourceFunction<SensorReading> mySource = new SourceFunction<SensorReading>() {
            boolean flag = true;
            @Override
            public void run(SourceContext<SensorReading> ctx) throws Exception {
                Random random = new Random();
                while (flag) {
                    ctx.collect(new SensorReading("sensor_" + (random.nextInt(3) + 1),
                            System.currentTimeMillis(), random.nextDouble() + 20));
                    Thread.sleep(1000);
                }
            }

            @Override
            public void cancel() {
                flag = false;
            }
        };
        DataStreamSource<SensorReading> streamSource4 = env.addSource(mySource);
        streamSource4.print("fromMySource");


        env.execute("sourceApp");
    }
}
