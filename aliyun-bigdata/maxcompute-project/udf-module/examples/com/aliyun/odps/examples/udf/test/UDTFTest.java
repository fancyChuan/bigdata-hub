package com.aliyun.odps.examples.udf.test;

import com.aliyun.odps.examples.TestUtil;
import com.aliyun.odps.udf.local.datasource.InputSource;
import com.aliyun.odps.udf.local.datasource.TableInputSource;
import com.aliyun.odps.udf.local.runner.BaseRunner;
import com.aliyun.odps.udf.local.runner.UDTFRunner;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class UDTFTest {

  @BeforeClass
  public static void initWarehouse() {
    TestUtil.initWarehouse();
  }

  @Test
  public void simpleInput() throws Exception{
    BaseRunner runner = new UDTFRunner(null, "com.aliyun.odps.examples.udf.UDTFExample");
    runner.feed(new Object[] { "one", "one" }).feed(new Object[] { "three", "three" })
        .feed(new Object[] { "four", "four" });
    List<Object[]> out = runner.yield();
    Assert.assertEquals(3, out.size());
    Assert.assertEquals("one,3", TestUtil.join(out.get(0)));
    Assert.assertEquals("three,5", TestUtil.join(out.get(1)));
    Assert.assertEquals("four,4", TestUtil.join(out.get(2)));
  }

  @Test
  public void inputFromTable() throws Exception{
    BaseRunner runner = new UDTFRunner(TestUtil.getOdps(), "com.aliyun.odps.examples.udf.UDTFExample");
    String project = "example_project";
    String table = "wc_in2";
    String[] partitions = new String[] { "p2=1", "p1=2" };
    String[] columns = new String[] { "colc", "cola" };

    InputSource inputSource = new TableInputSource(project, table, partitions, columns);
    Object[] data;
    while ((data = inputSource.getNextRow()) != null) {
      runner.feed(data);
    }
    List<Object[]> out = runner.yield();
    Assert.assertEquals(3, out.size());
    Assert.assertEquals("three3,6", TestUtil.join(out.get(0)));
    Assert.assertEquals("three3,6", TestUtil.join(out.get(1)));
    Assert.assertEquals("three3,6", TestUtil.join(out.get(2)));
  }

  @Test
  public void resourceTest() throws Exception{
    BaseRunner runner = new UDTFRunner(TestUtil.getOdps(), "com.aliyun.odps.examples.udf.UDTFResource");
    runner.feed(new Object[] { "one", "one" }).feed(new Object[] { "three", "three" })
        .feed(new Object[] { "four", "four" });
    List<Object[]> out = runner.yield();
    Assert.assertEquals(3 + "", out.size() + "");
    Assert.assertEquals("one,3,fileResourceLineCount=3|tableResource1RecordCount=4|tableResource2RecordCount=4",
        TestUtil.join(out.get(0)));
    Assert.assertEquals("three,5,fileResourceLineCount=3|tableResource1RecordCount=4|tableResource2RecordCount=4",
        TestUtil.join(out.get(1)));
    Assert.assertEquals("four,4,fileResourceLineCount=3|tableResource1RecordCount=4|tableResource2RecordCount=4",
        TestUtil.join(out.get(2)));
  }

}