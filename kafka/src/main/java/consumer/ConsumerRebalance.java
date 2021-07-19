package consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

/**
 * 演示消费者的Rebalance机制
 *  场景：同个消费者组，先后启动消费者
 *  结果：
 *      1. 第1个消费者启动后，会拿到所有的分区，此时onPartitionsRevoked方法不会拿到分区（因为还没有分配过）
 *      2. 第2个消费启动的时候，就会Rebalance了。
 *          这时，消费者1的onPartitionsRevoked会拿到上一步骤被分配的分区，比如有6个，分别为p0/p1/p2/p3/p4/p5
 *          然后使用默认的Range分配策略，在onPartitionsAssigned方法拿到被重新分配的分区，有3个，分别为p0/p1/p2
 *          而第2个消费者，onPartitionsRevoked为空，onPartitionsAssigned拿到3个分区，分别为p3/p4/p5
 */
public class ConsumerRebalance {
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
                for (TopicPartition topicPartition : collection) {
                    System.out.println("partition" + topicPartition);
                }
            }

            // 该方法在Rebalance之后调用
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                System.out.println("================= Rebalance之后 ===============");
                for (TopicPartition topicPartition : collection) {
                    System.out.println("partition" + topicPartition);
                }
            }
        });

        while (true) {
            // 读取数据，读取超时时间为100ms
            ConsumerRecords<String, String> consumerRecords = consumer.poll(100);
            for (ConsumerRecord<String, String> record : consumerRecords) {
                System.out.println(record.topic() + "--"
                        + record.partition() + "--"
                        + record.value());
            }
            consumer.commitSync(); // 同步提交，当前线程会阻塞直到offset提交成功
        }
    }
}
