package com.aliyun.odps.examples.udf;

import com.aliyun.odps.udf.UDF;

public class UDFExample extends UDF {

  /**
   * project: example_project
   * table: wc_in1
   * columns: col1
   */
  public String evaluate(String a) {
    return "s2s:" + a;
  }

  /**
   * project: example_project 
   * table: wc_in1 
   * columns: col1,col2
   */
  public String evaluate(String a, String b) {
    return "ss2s:" + a + "," + b;
  }

  /**
   * project: example_project 
   * table: wc_in2 
   * partitions: p2=1,p1=2 
   * columns: colc,colb,cola
   */
  public String evaluate(String a, String b, String c) {
    return "sss2s:" + a + "," + b + "," + c;
  }

}
