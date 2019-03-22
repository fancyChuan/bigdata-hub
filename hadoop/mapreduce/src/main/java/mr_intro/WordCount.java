package mr_intro;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
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
        public void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
                throws IOException, InterruptedException {
            // line value
            String lineValue = value.toString();

            StringTokenizer stringTokenizer = new StringTokenizer(lineValue);

            while(stringTokenizer.hasMoreTokens()) {
                String wordValue = stringTokenizer.nextToken();
                mapOutputKey.set(wordValue);
                context.write(mapOutputKey, mapOutputValue);
            }
        }
    }


    // step2 reduce
    private static class WCReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private   IntWritable outputValue = new IntWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values,
                              Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable value: values) {
                sum += value.get();
            }
            outputValue.set(sum);
            System.out.println(key + ": " + outputValue);
            // context.write(key, outputValue);
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
        // input -> map -> reduce -> output
        Path inPath = new Path(args[0]);
        FileInputFormat.addInputPath(job, inPath);

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

        Path outPath = new Path(args[1]);
        FileOutputFormat.setOutputPath(job, outPath);

        boolean isSucc = job.waitForCompletion(true);
        return isSucc ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        String[] params = {"hadoop/mapreduce/src/main/java/mr_intro/wordcount.txt", "out"};
        int status = new WordCount().run(params);
        System.exit(status);
    }
}
