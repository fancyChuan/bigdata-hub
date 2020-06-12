package com.aliyun.odps.examples.udf;

import com.aliyun.odps.udf.UDFException;
import com.aliyun.odps.udf.UDTF;
import com.aliyun.odps.udf.annotation.Resolve;

/**
 * project: example_project 
 * table: wc_in2 
 * partitions: p2=1,p1=2 
 * columns: colc,colb
 */
@Resolve({"string,string->string,bigint"})
public class UDTFExample extends UDTF {

  @Override
  public void process(Object[] args) throws UDFException {
    String a = (String) args[0];
    long b = args[1] == null ? 0 : ((String) args[1]).length();

    forward(a, b);

  }
}
