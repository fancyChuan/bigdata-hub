package cn.fancychuan.hbase.mr;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;

import java.io.IOException;

/**
 * 读取HBase的fruit表中的数据
 *  1. 读取HBase的表，而不是文本文件，因此Mapper的泛型不能使用默认的TextInputFormat
 *  2. 在HBase中，默认使用TableInputFormat输入格式
 *      切片：一个region切一片
 *      RecordReader的泛型应该为：RecordReader<ImmutableBytesWritable, Result>
 *  3. hbase提供了一个简便的抽象类：TableMapper
 *      TableMapper<KEYOUT, VALUEOUT> extends Mapper<ImmutableBytesWritable, Result, KEYOUT, VALUEOUT>
 *  4. 输出的数据应该是一个put对象
 *      在Mapper中，数据如果需要排序，必须作为key，否则可以作为value
 */
public class ReadFruitMapper extends TableMapper<ImmutableBytesWritable, Put> {
    // 如果map的输出key或者value为空，那么就用NullWritable
    private NullWritable outKey = NullWritable.get();

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        // key.get() 等价于 key.copyBytes()
        Put put = new Put(key.get());
        Cell[] cells = value.rawCells();
        for (Cell cell : cells) {
            if("info".equals(Bytes.toString(CellUtil.cloneFamily(cell)))) {
                if("name".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    put.add(cell);
                } else if("color".equals(Bytes.toString(CellUtil.cloneFamily(cell)))) {
                    put.add(cell);
                }
            }
        }
        context.write(key, put);
    }
}
