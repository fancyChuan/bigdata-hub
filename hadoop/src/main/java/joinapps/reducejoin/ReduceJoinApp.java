package joinapps.reducejoin;

import joinapps.bean.OrderBean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Iterator;

public class ReduceJoinApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length == 0) {
            args = new String [] {"hadoop/input/forJoin", "hadoop/target/reducejoin"};
        }

        Job job = Job.getInstance(new Configuration());

        job.setJarByClass(ReduceJoinApp.class);
        job.setMapperClass(ReduceJoinMapper.class);
        job.setReducerClass(ReduceJoinReducer.class);

        job.setMapOutputKeyClass(OrderBean.class);
        job.setMapOutputValueClass(NullWritable.class);
        job.setOutputKeyClass(OrderBean.class);
        job.setOutputValueClass(NullWritable.class);

        job.setGroupingComparatorClass(ReduceJoinComparator.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }


    static class ReduceJoinMapper extends Mapper<LongWritable, Text, OrderBean, NullWritable> {
        private OrderBean orderBean = new OrderBean();
        private String fileName;

        @Override // 在任务开始的时候会执行一次，之后多次执行map
        protected void setup(Context context) throws IOException, InterruptedException {
            FileSplit fs = (FileSplit) context.getInputSplit();
            fileName = fs.getPath().getName();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] fields = value.toString().split("\t");
            if (fileName.equals("t_order.txt")) {
                orderBean.setId(fields[0]);
                orderBean.setPid(fields[1]);
                orderBean.setAmount(Integer.parseInt(fields[2]));
                orderBean.setPname("");
            } else {
                orderBean.setPid(fields[0]);
                orderBean.setPname(fields[1]);
                orderBean.setAmount(0);
                orderBean.setId("");
            }
            context.write(orderBean, NullWritable.get());
        }
    }

    static class ReduceJoinComparator extends WritableComparator {
        public ReduceJoinComparator() {
            super(OrderBean.class, true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            OrderBean oa = (OrderBean) a;
            OrderBean ob = (OrderBean) b;
            return oa.getPid().compareTo(ob.getPid());
        }
    }

    static class ReduceJoinReducer extends Reducer<OrderBean, NullWritable, OrderBean, NullWritable> {
        /**
         * todo: reduce这部分是重点，需要好好品味
         *
         * 经过GroupingComparator之后，得到的样本为：
         *      01  小米
         *      1001    01  1
         *      1003    01  4
         *  也就是说，我们按照pid进行分组排序之后，第一个是t_product表中的数据，后面的部分是t_order表中的数据
         *  所以最开始需要iterator.next()把指针移到第一位，也就是 "01   小米" 然后拿到pname
         */
        @Override
        protected void reduce(OrderBean key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            Iterator<NullWritable> iterator = values.iterator();
            iterator.next();
            String pname = key.getPname();
            while (iterator.hasNext()) {
                iterator.next();
                key.setPname(pname);
                context.write(key, NullWritable.get());
            }
        }
    }
}
