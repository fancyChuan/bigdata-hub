package cn.fancychuan.hbase.mr;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.NullWritable;

import java.io.IOException;

/**
 * Reducer的任务就是将Mapper输出的所有记录写出
 *      Reducer输出的类型必须是Mutation，是固定的
 *      Mutation是所有写数据类型的父类
 */
public class Fruit2FuritMrReducer extends TableReducer<ImmutableBytesWritable, Put, NullWritable> {
    @Override
    protected void reduce(ImmutableBytesWritable key, Iterable<Put> values, Context context) throws IOException, InterruptedException {
        // 将读出来的没一行数据写入HBase的fruit_mr表中
        for (Put put: values) {
            context.write(NullWritable.get(), put);
        }
    }
}
