package mrapps.combiner;

import mapreduce.intro.WordCount;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class WordCountWithCombinerApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length == 0) {
            args = new String [] {"E:\\JavaWorkshop\\bigdata-learn\\hadoop\\input\\wordcount.txt", "hadoop/target/wc-combiner"};
        }

        Job job = Job.getInstance(new Configuration());

        job.setJarByClass(WordCountWithCombinerApp.class);
        job.setMapperClass(WordCount.WCMapper.class);
        job.setReducerClass(WordCount.WCReducer.class);

        // 方法1 使用自定义的Combiner
        job.setCombinerClass(WordCountCombiner.class);
        // 方法2 直接使用Reducer
        // job.setCombinerClass(WordCount.WCReducer.class);
        
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }
}
