package apps;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 实现倒排索引
 *
 * TODO：多job串联该怎么优雅的实现？这个地方是否可以JVM复用？
 *      setJarByClass的时候选择同一个Driver类是否会有问题？
 */
public class InvertedIndexApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        String inputDir = "hadoop/input/forInvertedIndex";
        String firstDir = "hadoop/target/inverted/firstDir";
        String secondDir = "hadoop/target/inverted/secondDir";

        Job firstJob = getFirstJob(inputDir, firstDir);
        Job secondJob = getSecondJob(firstDir, secondDir);

        System.out.println("启动第一次MR作业..");
        boolean firstStatus = firstJob.waitForCompletion(true);
        if (firstStatus) {
            System.out.println("第一次MR运行成功，开始启动第二次MR作业...");
            secondJob.waitForCompletion(true);
        }
    }
    public static Job getFirstJob(String inPath, String outPath) throws IOException {
        Job job = Job.getInstance(new Configuration());
        job.setJarByClass(InvertedIndexApp.class);
        job.setMapperClass(FirstIndexMapper.class);
        job.setReducerClass(FirstIndexReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        FileInputFormat.setInputPaths(job, new Path(inPath));
        FileOutputFormat.setOutputPath(job, new Path(outPath));
        return job;
    }

    public static Job getSecondJob(String inPath, String outPath) throws IOException {
        Job job = Job.getInstance(new Configuration());
        job.setJarByClass(InvertedIndexApp.class);
        job.setMapperClass(SecondIndexMapper.class);
        job.setReducerClass(SecondIndexReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.setInputPaths(job, new Path(inPath));
        FileOutputFormat.setOutputPath(job, new Path(outPath));
        return job;
    }

    static class FirstIndexMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
        private Text k = new Text();
        private LongWritable v = new LongWritable(1);
        private String fileName;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            FileSplit inputSplit = (FileSplit) context.getInputSplit();
            fileName = inputSplit.getPath().getName();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] items = value.toString().split(" ");
            for (String item : items) {
                k.set(item + "--" + fileName);
                context.write(k, v);
            }
        }
    }

    static class FirstIndexReducer extends Reducer<Text, LongWritable, Text, LongWritable> {
        private LongWritable result = new LongWritable();
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

    static class SecondIndexMapper extends Mapper<LongWritable, Text, Text, Text> {
        Text k = new Text();
        Text v = new Text();
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] items = value.toString().split("--");
            k.set(items[0]);
            v.set(items[1]);
            context.write(k, v);
        }
    }

    static class SecondIndexReducer extends Reducer<Text, Text, Text, Text> {
        Text v = new Text();
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            StringBuilder builder = new StringBuilder();
            for (Text value : values) {
                builder.append(value.toString().replace("\t", "-->") + "\t");
            }
            v.set(builder.toString());
            context.write(key, v);
        }
    }
}
