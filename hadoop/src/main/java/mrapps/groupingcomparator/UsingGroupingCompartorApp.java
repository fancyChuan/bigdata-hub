package mrapps.groupingcomparator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Iterator;

public class UsingGroupingCompartorApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length == 0) {
            args = new String[]{"E:\\JavaWorkshop\\bigdata-learn\\hadoop\\input\\GroupingComparator.txt", "hadoop/target/group"};
        }
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setGroupingComparatorClass(OrderComparator.class); // 这个地方使用GroupingComparator

        job.setJarByClass(UsingGroupingCompartorApp.class);
        job.setMapperClass(OrderMapper.class);
        job.setReducerClass(OrderReducer.class);

        job.setMapOutputKeyClass(OrderBean.class);
        job.setMapOutputValueClass(NullWritable.class);
        job.setOutputKeyClass(OrderBean.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }


    static class OrderMapper extends Mapper<LongWritable, Text, OrderBean, NullWritable> {
        private OrderBean bean = new OrderBean();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] items = value.toString().split("\t");
            bean.setOrderId(items[0]);
            bean.setProductId(items[1]);
            bean.setPrice(Double.parseDouble(items[2]));
            context.write(bean, NullWritable.get());
        }
    }

    static class OrderReducer extends Reducer<OrderBean, NullWritable, OrderBean, NullWritable> {
        @Override
        protected void reduce(OrderBean key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            // context.write(key, NullWritable.get());

            // TODO：需要好好捋一下为什么这个地方key其实也是多个。只不过因为key（也就是我们的OrderBean）的orderID相同
            // 其实这个也是跟反序列化机制有关的，都是先新建key/value两个对象，然后从文件中读出数据反序列化之后装到key/value中
            // 如果我们要拿到每个订单价格最高的前两个商品，那么代码如下：
            Iterator<NullWritable> iterator = values.iterator();
            for (int i = 0; i < 2; i++) {
                if (iterator.hasNext()) {
                    context.write(key, iterator.next());
                }
            }
        }
    }
}
