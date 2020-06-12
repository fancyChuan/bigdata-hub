package com.aliyun.odps.examples.mr.test;

import com.aliyun.odps.data.Record;
import com.aliyun.odps.data.TableInfo;
import com.aliyun.odps.examples.TestUtil;
import com.aliyun.odps.examples.mr.WordCount;
import com.aliyun.odps.io.Text;
import com.aliyun.odps.mapred.conf.JobConf;
import com.aliyun.odps.mapred.unittest.*;
import com.aliyun.odps.mapred.utils.InputUtils;
import com.aliyun.odps.mapred.utils.OutputUtils;
import com.aliyun.odps.mapred.utils.SchemaUtils;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class WordCountTest extends MRUnitTest {
  // 定义输入输出表的 schema
  private final static String INPUT_SCHEMA = "a:string,b:string";
  private final static String OUTPUT_SCHEMA = "k:string,v:bigint";
  private JobConf job;

  public WordCountTest() throws Exception {
    TestUtil.initWarehouse();
    // 准备作业配置
    job = new JobConf();

    job.setMapperClass(WordCount.TokenizerMapper.class);
    job.setCombinerClass(WordCount.SumCombiner.class);
    job.setReducerClass(WordCount.SumReducer.class);

    job.setMapOutputKeySchema(SchemaUtils.fromString("key:string"));
    job.setMapOutputValueSchema(SchemaUtils.fromString("value:bigint"));

    InputUtils.addTable(TableInfo.builder().tableName("wc_in").build(), job);
    OutputUtils.addTable(TableInfo.builder().tableName("wc_out").build(), job);
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testMap() throws IOException, ClassNotFoundException, InterruptedException {
    MapUTContext mapContext = new MapUTContext();
    mapContext.setInputSchema(INPUT_SCHEMA);
    mapContext.setOutputSchema(OUTPUT_SCHEMA, job);
    // 准备测试数据
    Record record = mapContext.createInputRecord();
    record.set(new Text[] {new Text("hello"), new Text("c")});
    mapContext.addInputRecord(record);

    record = mapContext.createInputRecord();
    record.set(new Text[] {new Text("hello"), new Text("java")});
    mapContext.addInputRecord(record);
    // 运行 map 过程
    TaskOutput output = runMapper(job, mapContext);

    // 验证 map 的结果（执行了combine），为 3 组 key/value 对
    List<KeyValue<Record, Record>> kvs = output.getOutputKeyValues();
    Assert.assertEquals(3, kvs.size());
    Assert.assertEquals(new KeyValue<String, Long>(new String("c"), new Long(1)),
        new KeyValue<String, Long>((String) (kvs.get(0).getKey().get(0)), (Long) (kvs.get(0)
            .getValue().get(0))));
    Assert.assertEquals(new KeyValue<String, Long>(new String("hello"), new Long(2)),
        new KeyValue<String, Long>((String) (kvs.get(1).getKey().get(0)), (Long) (kvs.get(1)
            .getValue().get(0))));
    Assert.assertEquals(new KeyValue<String, Long>(new String("java"), new Long(1)),
        new KeyValue<String, Long>((String) (kvs.get(2).getKey().get(0)), (Long) (kvs.get(2)
            .getValue().get(0))));
  }

  @Test
  public void testReduce() throws IOException, ClassNotFoundException, InterruptedException {
    ReduceUTContext context = new ReduceUTContext();
    context.setOutputSchema(OUTPUT_SCHEMA,  job);
    // 准备测试数据
    Record key = context.createInputKeyRecord(job);
    Record value = context.createInputValueRecord(job);
    key.set(0, "world");
    value.set(0, new Long(1));
    context.addInputKeyValue(key, value);
    key.set(0, "hello");
    value.set(0, new Long(1));
    context.addInputKeyValue(key, value);
    key.set(0, "hello");
    value.set(0, new Long(1));
    context.addInputKeyValue(key, value);
    key.set(0, "odps");
    value.set(0, new Long(1));
    context.addInputKeyValue(key, value);

    // 运行 reduce 过程
    TaskOutput output = runReducer(job, context);

    // 验证 reduce 结果，为 3 条 record
    List<Record> records = output.getOutputRecords();
    Assert.assertEquals(3, records.size());
    Assert.assertEquals(new String("hello"), records.get(0).get("k"));
    Assert.assertEquals(new Long(2), records.get(0).get("v"));
    Assert.assertEquals(new String("odps"), records.get(1).get("k"));
    Assert.assertEquals(new Long(1), records.get(1).get("v"));
    Assert.assertEquals(new String("world"), records.get(2).get("k"));
    Assert.assertEquals(new Long(1), records.get(2).get("v"));
  }

}