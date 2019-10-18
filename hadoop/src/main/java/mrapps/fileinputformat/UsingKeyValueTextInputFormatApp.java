package mrapps.fileinputformat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueLineRecordReader;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * KeyValueTextInputFormat的使用示例：统计文件每一行开头单词出现次数
 *
 * 注意：我们把Mapper类和Reducer类写到Driver类中的时候，千万要记得用static，否则作业提交到集群的时候会报错。
 *      也就是说需要使用静态内部类，因为Driver类，也就是UsingKeyValueTextInputFormatApp是没有经过实例化的.
 * 错误信息为：NoSuchMethodException: mrapps.fileinputformat.UsingKeyValueTextInputFormatApp$KVTextMapper.<init>()
 */
public class UsingKeyValueTextInputFormatApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length == 0) {
            args = new String[]{"E:\\JavaWorkshop\\bigdata-learn\\hadoop\\input\\docs\\quangle.txt", "hadoop/target/kvapp"};
        }

        Configuration conf = new Configuration();
        // 设置分隔符
        conf.set(KeyValueLineRecordReader.KEY_VALUE_SEPERATOR, " ");

        Job job = Job.getInstance(conf);
        job.setJarByClass(UsingKeyValueTextInputFormatApp.class);
        job.setMapperClass(KVTextMapper.class);
        job.setReducerClass(KVTextReduer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        // 输入路径
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        // **设置输入格式**
        job.setInputFormatClass(KeyValueTextInputFormat.class);
        // 输出路径
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }


    static class KVTextMapper extends Mapper<Text, Text, Text, LongWritable> {
        LongWritable v = new LongWritable(1);
        @Override
        protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
            System.out.println("map");
            context.write(key, v);
        }
    }
    static class KVTextReduer extends Reducer<Text, LongWritable, Text, LongWritable> {
        LongWritable result = new LongWritable();
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long sum = 0L;
            for (LongWritable value : values) {
                sum += value.get();
            }
            result.set(sum);
            System.out.println(key + ": " + result);
            context.write(key, result);
        }
    }
}
