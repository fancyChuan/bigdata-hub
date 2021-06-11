package consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

/**
 * 演示消费者使用Rebalance机制来自定义存储offset，有2个地方要考虑处理
 * 1.在重新均衡分组之前保存数，在重新均衡后读取数据
 * 2.在提交偏移量时保存数据
 */
public class ConsumerRebalanceOffset {
    private static HashMap<TopicPartition, Long> currentOffset = new HashMap();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop101:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "rebalance_groupid");
        // 关闭自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // key和value的反序列化
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList("test"), new ConsumerRebalanceListener() {
            // 该方法在Rebalance之前调用，会将该消费者已经被分配的分区“回收”，做相应的处理，然后Rebalance
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                System.out.println("================= Rebalance之前 ===============");
                commitOffset(currentOffset);
            }

            // 该方法在Rebalance之后调用
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> topicPartitions) {
                System.out.println("================= Rebalance之后 ===============");
                currentOffset.clear();
                for (TopicPartition topicPartition : topicPartitions) {
                    //定位到最近提交的offset位置继续消费
                    consumer.seek(topicPartition, getOffset(topicPartition));
                }
            }
        });

        while (true) {
            // 读取数据，读取超时时间为100ms
            ConsumerRecords<String, String> consumerRecords = consumer.poll(100);
            for (ConsumerRecord<String, String> record : consumerRecords) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());

                currentOffset.put(new TopicPartition(record.topic(), record.partition()), record.offset());
            }
            //异步提交
            commitOffset(currentOffset);
        }
    }

    /**
     * 获取某分区的最新offset
     */
    private static long getOffset(TopicPartition partition) {
        return 0;
    }

    /**
     * 提交该消费者所有分区的offset
     */
    private static void commitOffset(Map<TopicPartition, Long> currentOffset) {

    }
}
