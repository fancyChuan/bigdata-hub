package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 生产者：同步的方式发送数据
 */
public class NewProducerSync {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        // Kafka服务端的主机名和端口号
        props.put("bootstrap.servers", "hadoop101:9092,hadoop102:9092,hadoop103:9092");
        // 等待所有副本节点的应答
        props.put("acks", "all");
        // 消息发送最大尝试次数
        props.put("retries", 1);
        // 一批消息处理大小
        props.put("batch.size", 16384);
        // 请求延时（等待时间）
        // props.put("linger.ms", 1);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        // 发送缓存区内存大小（RecordAccumulator缓冲区大小）
        props.put("buffer.memory", 33554432);
        // key序列化
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // value序列化
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 配置拦截器链
        ArrayList<String> list = new ArrayList<>();
        // 拦截器谁先添加谁先执行
        list.add("intercetor.TimeIntercetor");
        list.add("intercetor.CountInterceptor");
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, list);

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>("test", "hello kafka sync - " + i);
            Future<RecordMetadata> send = producer.send(record);
            // 调用future的get方法就会阻塞
            RecordMetadata metadata = send.get();
            System.out.print("partition: " + metadata.partition() + "\t");
            System.out.println("offset: " + metadata.offset());
        }
        System.out.println("============ 主线程结束 =============");
        producer.close();
    }
}
