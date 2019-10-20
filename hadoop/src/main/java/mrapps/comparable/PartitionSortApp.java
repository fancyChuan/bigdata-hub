package mrapps.comparable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 通过自定义分区、自定义排序实现区内排序
 * 需求：
 *  按照每个省手机号输出的文件内按照总流量内部排序
 */
public class PartitionSortApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length == 0) {
            args = new String[]{"hadoop/target/flow/", "hadoop/target/flow-sort-part"};
        }
        Job job = Job.getInstance(new Configuration());

        job.setJarByClass(PartitionSortApp.class);
        job.setMapperClass(SelfComparableApp.SortMapper.class);
        job.setReducerClass(SelfComparableApp.SortReducer.class);
        // 使用自定义分区
        job.setPartitionerClass(MyPartitioner.class);
        job.setNumReduceTasks(5);

        job.setMapOutputKeyClass(FlowBeanComparable.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBeanComparable.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);

    }

    static class MyPartitioner extends Partitioner<FlowBeanComparable, Text> {
        @Override
        public int getPartition(FlowBeanComparable flowBeanComparable, Text text, int numPartitions) {
            switch (text.toString().substring(0, 3)) {
                case "136":
                    return 1;
                case "137":
                    return 0;
                case "138":
                    return 2;
                case "139":
                    return 3;
                default:
                    return 4;

            }
        }
    }
}
