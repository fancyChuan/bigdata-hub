package mrapps.fileinputformat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

import java.io.IOException;

/**
 * 使用自定义的FileInputFormat
 */
public class SelfFileInputForamtApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length == 0) {
            args = new String[]{"E:\\JavaWorkshop\\bigdata-learn\\hadoop\\input\\smallfiles", "hadoop/target/self"};
        }

        Job job = Job.getInstance(new Configuration());

        job.setJarByClass(SelfFileInputForamtApp.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(BytesWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(BytesWritable.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // 使用我们自定义的FileInputFormat
        job.setInputFormatClass(SelfFileInputFormat.class);
        // 输出文件格式设置为SequenceFileOutputFormat
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        job.waitForCompletion(true);
    }
}
