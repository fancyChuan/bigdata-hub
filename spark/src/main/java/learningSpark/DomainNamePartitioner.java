package learningSpark;

import org.apache.spark.Partitioner;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 自定义分区器，用来对url做分区
 */
public class DomainNamePartitioner extends Partitioner {
    private int partitions;

    public DomainNamePartitioner(int partitions) {
        this.partitions = partitions;
    }

    @Override
    public int numPartitions() {
        return partitions;
    }

    @Override
    public int getPartition(Object key) {
        try {
            String domain =  new URL(key.toString()).getHost();
            int code = domain.hashCode() % partitions;
            if (code < 0) {
                return code + partitions;
            } else {
                return code;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean equals(Partitioner other) {
        if (other instanceof DomainNamePartitioner) {
            return ((DomainNamePartitioner) other).numPartitions() == partitions;
        }
        return false;
    }
}
