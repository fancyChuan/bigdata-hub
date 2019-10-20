package mrapps.comparable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class SelfComparableApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length == 0) {
            args = new String[]{"hadoop/target/flow/", "hadoop/target/flow-sort"};
        }
        Job job = Job.getInstance(new Configuration());

        job.setJarByClass(SelfComparableApp.class);
        job.setMapperClass(SortMapper.class);
        job.setReducerClass(SortReducer.class);

        job.setMapOutputKeyClass(FlowBeanComparable.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBeanComparable.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }

    static class SortMapper extends Mapper<LongWritable, Text, FlowBeanComparable, Text> {
        FlowBeanComparable bean = new FlowBeanComparable();
        Text phone = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] items = value.toString().split("\t");
            phone.set(items[0]);
            bean.setUpFlow(Long.parseLong(items[1]));
            bean.setDownFlow(Long.parseLong(items[2]));
            bean.setSumFlow(Long.parseLong(items[3]));
            // 把bean作为key
            context.write(bean, phone);
        }
    }

    static class SortReducer extends Reducer<FlowBeanComparable, Text, Text, FlowBeanComparable> {
        @Override
        protected void reduce(FlowBeanComparable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text value : values) {
                context.write(value, key);
            }
        }
    }
}
