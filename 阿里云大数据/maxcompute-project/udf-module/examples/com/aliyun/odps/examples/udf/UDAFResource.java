package com.aliyun.odps.examples.udf;

import com.aliyun.odps.io.LongWritable;
import com.aliyun.odps.io.Text;
import com.aliyun.odps.io.Writable;
import com.aliyun.odps.udf.Aggregator;
import com.aliyun.odps.udf.ExecutionContext;
import com.aliyun.odps.udf.UDFException;
import com.aliyun.odps.udf.annotation.Resolve;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * project: example_project 
 * table: wc_in2 
 * partitions: p2=1,p1=2 
 * columns: colc,colb,cola
 */
@Resolve("string->bigint")
public class UDAFResource extends Aggregator {
  ExecutionContext ctx;
  long fileResourceLineCount;
  long tableResource1RecordCount;
  long tableResource2RecordCount;

  @Override
  public void setup(ExecutionContext ctx) throws UDFException {
    this.ctx = ctx;
    try {
      InputStream in = ctx.readResourceFileAsStream("file_resource.txt");
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      fileResourceLineCount = 0;
      String line;
      while ((line = br.readLine()) != null) {
        fileResourceLineCount++;
      }
      br.close();

      Iterator<Object[]> iterator = ctx.readResourceTable("table_resource1").iterator();
      tableResource1RecordCount = 0;
      while (iterator.hasNext()) {
        tableResource1RecordCount++;
        iterator.next();
      }

      iterator = ctx.readResourceTable("table_resource2").iterator();
      tableResource2RecordCount = 0;
      while (iterator.hasNext()) {
        tableResource2RecordCount++;
        iterator.next();
      }

    } catch (IOException e) {
      throw new UDFException(e);
    }
  }

  @Override
  public void iterate(Writable arg0, Writable[] arg1) throws UDFException {
    LongWritable result = (LongWritable) arg0;
    for (Writable item : arg1) {
      Text txt = (Text) item;
      result.set(result.get() + txt.getLength());
    }

  }

  @Override
  public void merge(Writable arg0, Writable arg1) throws UDFException {
    LongWritable result = (LongWritable) arg0;
    LongWritable partial = (LongWritable) arg1;
    result.set(result.get() + partial.get());

  }

  @Override
  public Writable newBuffer() {
    return new LongWritable(0L);
  }

  @Override
  public Writable terminate(Writable arg0) throws UDFException {
    LongWritable result = (LongWritable) arg0;
    result.set(result.get() + fileResourceLineCount + tableResource1RecordCount
        + tableResource2RecordCount);
    return result;
  }

}
