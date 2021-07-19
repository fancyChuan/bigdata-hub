package consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * 消费者API
 *  KafkaConsumer：需要创建一个消费者对象，用来消费数据
 *  ConsumerConfig：获取所需的一系列配置参数
 *  ConsuemrRecord：每条数据都要封装成一个ConsumerRecord对象
 */
public class Consumer {

    public static void main(String[] args) {
        Properties props = new Properties();
        // 定义kakfa 服务的地址，不需要将所有broker指定上
        props.put("bootstrap.servers", "hadoop101:9092");
        // 指定consumer group
        props.put("group.id", "test_groupid");
        // 重复消费的话，除了消费者组改名字，还要加下面一行
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 重置offset，earliest表示从头开始消费，默认是最新值latest
        // 是否自动确认offset
        props.put("enable.auto.commit", "false");
        // 自动确认offset的时间间隔
        props.put("auto.commit.interval.ms", "1000");
        // key的序列化类
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // value的序列化类
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        props.put("offsets.topic.replication.factor", 1);

        // 定义consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        // 消费者订阅的topic, 可同时订阅多个，比如同时订阅test、first两个topic
        consumer.subscribe(Arrays.asList("test", "first"));
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

            // consumer.commitSync(); // 同步提交，当前线程会阻塞直到offset提交成功
            // 异步提交，为保证吞吐量，一般会用异步提交offset
            consumer.commitAsync(new OffsetCommitCallback() {
                @Override
                public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                    if (exception != null) {
                        System.err.println("Commit failed for" + offsets);
                    }
                }
            });
        }
    }
}
