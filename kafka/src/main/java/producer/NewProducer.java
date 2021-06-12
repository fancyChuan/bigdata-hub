package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.Properties;

/**
 * 生产者：不带回调函数的send方法
 *  KafkaProducer：需要创建一个生产者对象，用来发送数据
 *  ProducerConfig：获取所需的一系列配置参数
 *  ProducerRecord：每条数据都要封装成一个ProducerRecord对象
 *
 *  TODO：为什么每次跑都会有一条数据失败？
 *  发送成功：21条数据
 *  发送失败：1条数据
 */
public class NewProducer {

    public static void main(String[] args) {
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
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        // 发送缓存区内存大小（RecordAccumulator缓冲区大小）
        props.put("buffer.memory", 33554432);
        // key序列化
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // value序列化
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 配置拦截器链
        ArrayList<String> list = new ArrayList<>();
        // 拦截器谁先添加谁先执行，下面这两个是自定义的拦截器
        // 拦截器：给每条数据前面加上时间戳
        list.add("intercetor.TimeIntercetor");
        // 拦截器：统计失败和成功发送的数据量
        list.add("intercetor.CountInterceptor");
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, list);

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 20; i++) {
            // 指定了分区号的ProducerRecord
            // ProducerRecord<String, String> record = new ProducerRecord<>("test", 1, String.valueOf(1), "hello kafka - " + i);
            // 不指定分区，数据会以轮询的方式存到各个分区
            ProducerRecord<String, String> record = new ProducerRecord<>("test", "enen, hello world-" + i);
            // 不指定分区，但指定了key，那么会以key的hash值跟分区数取余作为存放数据的分区号
            // ProducerRecord<String, String> record = new ProducerRecord<>("test", String.valueOf(i), "hello key-" + i);
            producer.send(record);
        }
        producer.close();
    }
}
