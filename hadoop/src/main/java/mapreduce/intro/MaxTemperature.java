package mapreduce.intro;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * job对象指定作业执行规范
 */
public class MaxTemperature {

    public static void main(String[] args) throws Exception {
        // testMaxTemperature(args);
        testMaxTemperatureWithCombiner(args);
    }

    public static void testMaxTemperature(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: MaxTemperature <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
//        conf.addResource("alone/core-site.xml");
//        conf.addResource("alone/hdfs-site.xml");
        Job job = Job.getInstance(conf, "FindMaxTemperature");
        // 1. 执行要运行的jar类
        job.setJarByClass(MaxTemperature.class); // 这行的作用是执行命令 hadoop MaxTemperature input output 的时候能够把对应的jar文件加载到集群中

         // 2. 配置输入输出的路径，可以实现多路径输入（多次调用addInputPaht()）
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        // 3. 设置mapper、reducer类
        job.setMapperClass(MaxTemperatureMapper.class);
        job.setReducerClass(MaxTemperatureReducer.class);
        // 4. 控制reduce函数的输出类型，需要与reduce类产生的输出想匹配
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        // 5. 提交任务并等待执行完成
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    public static void testMaxTemperatureWithCombiner(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: MaxTemperature <input path> <output path>");
            System.exit(-1);
        }

        Job job = new Job();
        job.setJarByClass(MaxTemperature.class);
        job.setJobName("Max Temperature With Combiner");

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(MaxTemperatureMapper.class);
        // 跟上一个函数相比，增加了下面这一行。也就是说，这个时候combiner和reducer功能一样，找到mapper输出中最大一个，最后再传到reducer中
        job.setCombinerClass(MaxTemperatureReducer.class);
        job.setReducerClass(MaxTemperatureReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
