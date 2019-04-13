package consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class Consumer {

    public static void main(String[] args) {
        Properties props = new Properties();
        // 定义kakfa 服务的地址，不需要将所有broker指定上
        props.put("bootstrap.servers", "s00:9092");
        // 制定consumer group
        props.put("group.id", "test");
        // 重复消费的话，除了消费者组改名字，还要加下面一行
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 重置offset，earliest表示从头开始消费，默认是最新值latest
        // 是否自动确认offset
        props.put("enable.auto.commit", "true");
        // 自动确认offset的时间间隔
        props.put("auto.commit.interval.ms", "1000");
        // key的序列化类
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // value的序列化类
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        props.put("offsets.topic.replication.factor", 1);

        // 定义consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        // 消费者订阅的topic, 可同时订阅多个
        consumer.subscribe(Arrays.asList("first", "second", "third"));
//        consumer.assign(Collections.singletonList(new TopicPartition("second", 0)));// 指定消费的主题和分区
//        consumer.seek(new TopicPartition("second", 0), 2); // 指定从哪个地方开始消费

        while (true) {
            // 读取数据，读取超时时间为100ms
            ConsumerRecords<String, String> records = consumer.poll(100);


            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.topic() + "--"
                        + record.partition() + "--"
                        + record.value());
            }
        }
    }
}
