package cn.fancychuan.hbase.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

/**
 * 目标：将fruit表中的一部分数据，通过MR迁入到fruit_mr表中
 */
public class Fruit2FuritMrApp extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();
        Job job = Job.getInstance(conf, this.getClass().getSimpleName());
        job.setJarByClass(Fruit2FuritMrApp.class);

        // 配置job
        Scan scan = new Scan();
        scan.setCacheBlocks(false);
        scan.setCaching(500);

        // 设置mapper
        TableMapReduceUtil.initTableMapperJob("fruit",  // 数据源的表名
                scan,   // scan扫描控制器
                Fruit2FuritMrMapper.class,
                ImmutableBytesWritable.class,
                Put.class,
                job     // 设置给哪个Job
        );
        TableMapReduceUtil.initTableReducerJob("fruit_mr", Fruit2FuritMrReducer.class, job);

        job.setNumReduceTasks(1);

        boolean isSuccess = job.waitForCompletion(true);
        if (!isSuccess) {
            throw new IOException("job running with error");
        }
        return isSuccess ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        int status = ToolRunner.run(conf, new Fruit2FuritMrApp(), args);
        System.exit(status);
    }

}
