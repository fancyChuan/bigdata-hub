package producer;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class NewProducerCallback {
    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        // Kafka服务端的主机名和端口号
        props.put("bootstrap.servers", "s00:9092");
        // 等待所有副本节点的应答
        props.put("acks", "all");
        // 消息发送最大尝试次数
        props.put("retries", 0);
        // 一批消息处理大小
        props.put("batch.size", 16384);
        // 增加服务端请求延时
        props.put("linger.ms", 1);
        // 发送缓存区内存大小
        props.put("buffer.memory", 33554432);
        // key序列化
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // value序列化
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 跟自定义分区绑定。有两种写法
        // props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "producer.SelfPartitioner");
        // props.put("partitioner.class", "producer.SelfPartitioner");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(props);

        for (int i = 0; i < 5; i++) {
            // 为什么回调这部分不能打印出来？在调试的时候，稍等一会才可以? 这是因为后面没有加上 producer.close() 导致程序执行完了，但是客户端不确定是否要发到broker上去
            kafkaProducer.send(new ProducerRecord<String, String>("test", "hello back2 - " + i), new Callback() {

                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception == null) {
                        System.out.println(metadata.partition() + "---" + metadata.offset());
                    }
                }
            });
        }

//        Thread.sleep(1000); // 要么休眠（模拟生产常驻进程），要么关闭生产者，否则数据无法发送到broker
        System.out.println("done!");
        kafkaProducer.close(); // 这个地方也要关闭资源

    }
}
