package com.aliyun.odps.examples.udf;

import com.aliyun.odps.io.LongWritable;
import com.aliyun.odps.io.Text;
import com.aliyun.odps.io.Writable;
import com.aliyun.odps.udf.Aggregator;
import com.aliyun.odps.udf.UDFException;
import com.aliyun.odps.udf.annotation.Resolve;

/**
 * project: example_project 
 * table: wc_in2 
 * partitions: p2=1,p1=2 
 * columns: colc,colb,cola
 */
@Resolve("string->bigint")
public class UDAFExample extends Aggregator {

  @Override
  public void iterate(Writable buffer, Writable[] args) throws UDFException {
    LongWritable result = (LongWritable) buffer;
    for (Writable item : args) {
      Text txt = (Text) item;
      result.set(result.get() + txt.getLength());
    }

  }

  @Override
  public void merge(Writable buffer, Writable partial) throws UDFException {
    LongWritable result = (LongWritable) buffer;
    LongWritable partialResult = (LongWritable) partial;
    result.set(result.get() + partialResult.get());

  }

  @Override
  public Writable newBuffer() {
    return new LongWritable(0L);
  }

  @Override
  public Writable terminate(Writable buffer) throws UDFException {
    return buffer;
  }

}
