package com.aliyun.odps.examples.unstructured;

import com.aliyun.odps.data.Record;
import com.aliyun.odps.io.OutputStreamSet;
import com.aliyun.odps.io.SinkOutputStream;
import com.aliyun.odps.udf.DataAttributes;
import com.aliyun.odps.udf.ExecutionContext;
import com.aliyun.odps.udf.Outputer;

import java.io.IOException;

public class TextOutputer extends Outputer {
  private SinkOutputStream outputStream;
  private DataAttributes attributes;
  private String delimiter;

  public TextOutputer (){
    // default delimiter, this can be overwritten if a delimiter is provided through the attributes.
    this.delimiter = "|";
  }

  @Override
  public void output(Record record) throws IOException {
    this.outputStream.write(recordToString(record).getBytes());
  }

  // no particular usage of execution context in this example
  @Override
  public void setup(ExecutionContext ctx, OutputStreamSet outputStreamSet, DataAttributes attributes) throws IOException {
    this.outputStream = outputStreamSet.next();
    this.attributes = attributes;
  }

  @Override
  public void close() {
    // no-op
  }

  private String recordToString(Record record){
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < record.getColumnCount(); i++)
    {
      if (null == record.get(i)){
        sb.append("NULL");
      }
      else{
        sb.append(record.get(i).toString());
      }
      if (i != record.getColumnCount() - 1){
        sb.append(this.delimiter);
      }
    }
    sb.append("\n");
    return sb.toString();
  }
}
