package producer;


import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

/**
 * 自定义分区
 */
public class SelfPartitioner implements Partitioner {
    private Map confMap;

    /**
     * 自定义分区的核心逻辑，返回值就是分区的编号
     */
    @Override
    public int partition(String s, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
        // confMap.get();
        return 0; // 表示把数据放到0号分区
    }

    @Override
    public void close() {

    }

    /**
     * 传递一些配置资源以供使用
     */
    @Override
    public void configure(Map<String, ?> map) {
        confMap = map;
    }
}
