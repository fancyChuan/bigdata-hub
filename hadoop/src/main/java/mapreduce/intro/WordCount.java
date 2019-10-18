package mapreduce.intro;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

    // step 1: map class
    // public class Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
    public static class WCMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private Text mapOutputKey = new Text();
        private final static  IntWritable mapOutputValue = new IntWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // 每一行的数据
            String lineValue = value.toString();
            // 使用这个类来切分行数据的原因是效率很高。而splite使用的是正则表达式，数据量增大的时候效率会降低。但是JDK不推荐使用，不知道为啥
            StringTokenizer stringTokenizer = new StringTokenizer(lineValue);
            while(stringTokenizer.hasMoreTokens()) {
                String wordValue = stringTokenizer.nextToken();
                mapOutputKey.set(wordValue); // 也可以写成 new Text(wordValue)，但是一般不这么写，因为处理大数据量的时候会大量生成新的对象，容易导致GC从而影响性能
                // context是MR的运行上下文，map阶段需要把数据传给reduce阶段
                context.write(mapOutputKey, mapOutputValue);
            }
        }
    }


    // step2 reduce
    private static class WCReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private   IntWritable outputValue = new IntWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable value: values) {
                sum += value.get();
            }
            outputValue.set(sum);
            System.out.println(key + ": " + outputValue);
            context.write(key, outputValue); // 这里上下文把结果写入文件系统
        }
    }

    // step 3
    public  int run(String[] args) throws Exception {
        // 1: get configuration
        Configuration conf = new Configuration();
        // MR中的压缩是可选项，属于配置范畴的
        // conf.set("mapreduce.map.output.compress", "true");
        // conf.set("mapreduce.map.output.codec", "org.apache.hadoop.io.compress.SnappyCodec");

        // 2: create job
        Job job = Job.getInstance(conf, this.getClass().getSimpleName());
        job.setJarByClass(this.getClass());

        // 3: set job :
        // 配置Mapper
        job.setMapperClass(WCMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // shuffle 过程
        // 1) partition
        // job.setPartitionerClass(cls);
        // 2) sort
        // job.setSortComparatorClass(cls);
        // 3) option combiner
        // job.setCombinerClass(cls);
        // 4) group
        // job.setGroupingComparatorClass(cls);

        // 配置reduce
        job.setReducerClass(WCReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        // reduce 优化
        // 增加reduce的个数
        // job.setNumReduceTasks(2);

        // 配置输入输出流
        Path inPath = new Path(args[0]);
        FileInputFormat.addInputPath(job, inPath);
        Path outPath = new Path(args[1]);
        FileOutputFormat.setOutputPath(job, outPath);
        // 提交job
        boolean isSucc = job.waitForCompletion(true);
        return isSucc ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            args = new String [] {"E:\\JavaWorkshop\\bigdata-learn\\hadoop\\input\\wordcount.txt", "hadoop/target/wordcount"};
        }
        int status = new WordCount().run(args);
        System.exit(status);
    }
}
