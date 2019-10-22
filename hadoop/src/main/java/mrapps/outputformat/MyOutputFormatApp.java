package mrapps.outputformat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 自定义OutputFormat
 *
 * 需求：
 *  把文本中含有baidu的写到want.txt文件，其他写到other.txt文件
 */
public class MyOutputFormatApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length == 0) {
            args = new String[]{"E:\\JavaWorkshop\\bigdata-learn\\hadoop\\input\\phone_data.txt", "hadoop/target/outputformat"};
        }

        Job job = Job.getInstance(new Configuration());

        // 这里的map和reduce都使用默认的
        job.setJarByClass(MyOutputFormatApp.class);
        // 设置我们自定义的outputFormat
        // job.setOutputFormatClass(MyOutputFormat.class);
        job.setOutputFormatClass(MyOutputFormatHDFS.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }
}
