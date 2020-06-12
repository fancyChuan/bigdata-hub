package com.aliyun.odps.examples.udf;

import com.aliyun.odps.udf.ExecutionContext;
import com.aliyun.odps.udf.UDFException;
import com.aliyun.odps.udf.UDTF;
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
 * columns: colc,colb
 */
@Resolve({"string,string->string,bigint,string"})
public class UDTFResource extends UDTF {
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
      String line;
      fileResourceLineCount = 0;
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
  public void process(Object[] args) throws UDFException {
    String a = (String) args[0];
    long b = args[1] == null ? 0 : ((String) args[1]).length();

    forward(a, b, "fileResourceLineCount=" + fileResourceLineCount + "|tableResource1RecordCount="
        + tableResource1RecordCount + "|tableResource2RecordCount=" + tableResource2RecordCount);

  }
}
