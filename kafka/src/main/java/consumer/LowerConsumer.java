package consumer;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.cluster.BrokerEndPoint;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LowerConsumer {

    public static void main(String[] args) {

        ArrayList<String> brokers = new ArrayList<>(); // Kafka集群的broker
        brokers.add("s00");

        int port = 9092;
        String topic = "first";
        int partition = 0;
        long offset = 2; // 这个数据在生产环境一般都会保存在一个地方，比如mysql，运行时从mysql取值，消费完以后把最新的offset存回mysql
        LowerConsumer lowerConsumer = new LowerConsumer();
        lowerConsumer.getData(brokers, port, topic, partition, offset);

    }

    public BrokerEndPoint findLeader(List<String> brokers, int port, String toppic, int partition) {
        for (String broker : brokers) {
            // 获取分区leader的消费者对象
            SimpleConsumer getLeader = new SimpleConsumer(broker, port, 100, 1024 * 4, "getLeader");
            // 创建一个主题元数据信息请求
            TopicMetadataRequest topicMetadataRequest = new TopicMetadataRequest(Collections.singletonList(toppic));
            // 获取并解析主题元数据返回值
            TopicMetadataResponse metadataResponse = getLeader.send(topicMetadataRequest);
            List<TopicMetadata> topicsMetadata = metadataResponse.topicsMetadata();
            // 遍历元数据
            for (TopicMetadata topicMetadata : topicsMetadata) {
                // 多个分区的元数据
                List<PartitionMetadata> partitionsMetadata = topicMetadata.partitionsMetadata();
                // 遍历分区，找到指定的分区
                for (PartitionMetadata partitionMetadata : partitionsMetadata) {
                    if (partition == partitionMetadata.partitionId()) {
                        return partitionMetadata.leader();
                    }
                }
            }
        }
        return null;
    }

    public void getData(List<String> brokers, int port, String topic, int partition, long offset) {
        BrokerEndPoint leader = findLeader(brokers, port, topic, partition);
        if (leader == null) {
            return ;
        }

        String leaderHost = leader.host();
        SimpleConsumer getData = new SimpleConsumer(leaderHost, port, 1000, 1024 * 4, "getData");
        // 可以一次性获取多个主题多个分区 new FetchRequestBuilder().addFetch().addFetch().addFetch()
        // fetchSize是获取的消息的字节数
        FetchRequest fetchRequest = new FetchRequestBuilder().addFetch(topic, partition, offset, 500000).build();

        FetchResponse fetchResponse = getData.fetch(fetchRequest);

        ByteBufferMessageSet messageAndOffsets = fetchResponse.messageSet(topic, partition);
        for (MessageAndOffset messageAndOffset : messageAndOffsets) {
            long offset1 = messageAndOffset.offset();
            ByteBuffer payload = messageAndOffset.message().payload();
            byte[] bytes = new byte[payload.limit()];
            payload.get(bytes);
            System.out.println(offset1 + "--" + new String(bytes));
        }
    }
}
