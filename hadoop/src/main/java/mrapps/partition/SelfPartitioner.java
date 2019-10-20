package mrapps.partition;

import mrapps.flow.FlowBean;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * 自定义分区
 */
public class SelfPartitioner extends Partitioner<Text, FlowBean> {
    @Override
    public int getPartition(Text text, FlowBean flowBean, int numPartitions) {
        String phone = text.toString();
        switch (phone.substring(0, 3)) { // 这个地方是5个分区，如果有4个ReduceTask，会报错；而如果是6个ReduceTask，那么不报错，只是有一个ReduceTask不干活，浪费了资源
            case "136":
                return 0;
            case "137":
                return 1;
            case "138":
                return 2;
            case "139":
                return 3;
            default:
                return 4;
        }
    }
}
