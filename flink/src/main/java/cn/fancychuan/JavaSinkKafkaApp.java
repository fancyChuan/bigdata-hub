package cn.fancychuan;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;

import java.util.Properties;

/**
 * 读取topic：sensor
 * 写入topic：flinksink
 * 注意这个程序没有容错机制，但sensor这个topic中的内容不是按照SensorReading的要求时，
 * flink应用会失败退出
 */
public class JavaSinkKafkaApp {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 从文本中读取
        // DataStream<String> inputStream = env.readTextFile("E:\\JavaWorkshop\\bigdata-learn\\flink\\src\\main\\resources\\sensor.txt");

        // 从kafka中读取
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "hadoop101:9092");
        properties.setProperty("group.id", "consumer-group");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("auto.offset.reset", "latest");
        DataStreamSource<String> inputStream = env.addSource(new FlinkKafkaConsumer011<String>("sensor", new SimpleStringSchema(), properties));

        DataStream<String> dataStream = inputStream.map(line -> {
            String[] items = line.split(",");
            return new SensorReading(items[0], Long.parseLong(items[1]), new Double(items[2])).toString();
        });

        dataStream.print("data");
        // 将结果写入Kafka
        dataStream.addSink(new FlinkKafkaProducer011<String>("hadoop101:9092", "flinksink", new SimpleStringSchema()));

        env.execute();
    }
}
