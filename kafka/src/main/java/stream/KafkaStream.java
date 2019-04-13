package stream;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.TopologyBuilder;

import java.util.Properties;

/**
 * 清洗数据的Kafka Stream
 */
public class KafkaStream {

    public static void main(String[] args) {

        // 创建拓扑对象
        TopologyBuilder builder = new TopologyBuilder();
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "s00:9092");
        properties.put("application.id", "KafkaStream");

        // 构建拓扑结构
        builder.addSource("SOURCE", "first")
                .addProcessor("PROCESSOR", () -> new LogProcessor(){}, "SOURCE")
                .addSink("SINK", "second", "PROCESSOR");

        KafkaStreams kafkaStreams = new KafkaStreams(builder, properties);
        kafkaStreams.start();
    }
}
