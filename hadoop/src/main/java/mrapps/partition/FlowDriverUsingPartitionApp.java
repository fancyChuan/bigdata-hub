package mrapps.partition;

import mrapps.flow.FlowBean;
import mrapps.flow.FlowDriver;
import mrapps.flow.FlowMapper;
import mrapps.flow.FlowReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 使用自定义的分区方法
 * 需求：
 *  手机号开头为136、137、138、139的分别单独写到一个分区(文件)中，其他的全部写到一个文件中
 */
public class FlowDriverUsingPartitionApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length == 0) {
            args = new String[]{"E:\\JavaWorkshop\\bigdata-learn\\hadoop\\input\\phone_data.txt", "hadoop/target/flow-part"};
        }

        Job job = Job.getInstance(new Configuration());

        job.setNumReduceTasks(5); // 这是reducer的并行数
        job.setPartitionerClass(SelfPartitioner.class); // 使用自定义的分区方法

        job.setJarByClass(FlowDriver.class);
        job.setMapperClass(FlowMapper.class);
        job.setReducerClass(FlowReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }
}
