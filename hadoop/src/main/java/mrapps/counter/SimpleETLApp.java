package mrapps.counter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 简单计数器的应用
 * 需求：去除日志长度小于等于11的日志
 */
public class SimpleETLApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        args = new String [] {"hadoop/input/weblog.txt", "hadoop/target/counter/"};

        Job job = Job.getInstance(new Configuration());

        job.setJarByClass(SimpleETLApp.class);
        job.setMapperClass(ETLMapper.class);
        job.setNumReduceTasks(0);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }

    static class ETLMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] items = value.toString().split(" ");
            if (items.length > 11) {
                context.write(value, NullWritable.get());
                context.getCounter("ETL", "True").increment(1);
            } else {
                context.getCounter("ETL", "False").increment(1);
            }
        }
    }
}
