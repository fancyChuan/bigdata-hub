package mrapps.fileinputformat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 设置一个切片为3行
 */
public class UsingNlineInputFormatApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length == 0) {
            args = new String [] {"E:\\JavaWorkshop\\bigdata-learn\\hadoop\\input\\wordcount.txt", "hadoop/target/nline/"};
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        // 设置每个切片InputSplit中划分三条记录
        NLineInputFormat.setNumLinesPerSplit(job, 3);
        // 使用NLineInputFormat处理记录数
        job.setInputFormatClass(NLineInputFormat.class);

        job.setJarByClass(UsingNlineInputFormatApp.class);
        job.setMapperClass(NLineMapper.class);
        job.setReducerClass(NLineReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);

    }


    static class NLineMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
        Text k = new Text();
        LongWritable v = new LongWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] items = line.split(" ");
            for (String item : items) {
                k.set(item);
                context.write(k, v);
            }
        }
    }
    static class NLineReducer extends Reducer<Text, LongWritable, Text, LongWritable> {
        LongWritable result = new LongWritable();

        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long sum = 0;
            for (LongWritable value : values) {
                sum += value.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }
}
