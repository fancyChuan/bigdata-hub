package com.aliyun.odps.examples.udf.test;

import com.aliyun.odps.examples.TestUtil;
import com.aliyun.odps.udf.local.datasource.InputSource;
import com.aliyun.odps.udf.local.datasource.TableInputSource;
import com.aliyun.odps.udf.local.runner.BaseRunner;
import com.aliyun.odps.udf.local.runner.UDFRunner;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class UDFTest {

  @BeforeClass
  public static void initWarehouse() {
    TestUtil.initWarehouse();
  }

  @Test
  public void simpleInput() throws Exception{
    BaseRunner runner = new UDFRunner(null, "com.aliyun.odps.examples.udf.UDFExample");
    runner.feed(new Object[] { "one", "one" }).feed(new Object[] { "three", "three" })
        .feed(new Object[] { "four", "four" });
    List<Object[]> out = runner.yield();

    Assert.assertEquals(3, out.size());
    Assert.assertEquals("ss2s:one,one", TestUtil.join(out.get(0)));
    Assert.assertEquals("ss2s:three,three", TestUtil.join(out.get(1)));
    Assert.assertEquals("ss2s:four,four", TestUtil.join(out.get(2)));
  }

  @Test
  public void inputFromTable() throws Exception{
    BaseRunner runner = new UDFRunner(TestUtil.getOdps(), "com.aliyun.odps.examples.udf.UDFExample");
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
    Assert.assertEquals("ss2s:three3,three1", TestUtil.join(out.get(0)));
    Assert.assertEquals("ss2s:three3,three1", TestUtil.join(out.get(1)));
    Assert.assertEquals("ss2s:three3,three1", TestUtil.join(out.get(2)));
  }

  @Test
  public void resourceTest() throws Exception{
    BaseRunner runner = new UDFRunner(TestUtil.getOdps(), "com.aliyun.odps.examples.udf.UDFResource");
    runner.feed(new Object[] { "one", "one" }).feed(new Object[] { "three", "three" })
        .feed(new Object[] { "four", "four" });
    List<Object[]> out = runner.yield();

    Assert.assertEquals(3, out.size());
    Assert.assertEquals("ss2s:one,one|fileResourceLineCount=3|tableResource1RecordCount=4|tableResource2RecordCount=4",
        TestUtil.join(out.get(0)));
    Assert.assertEquals("ss2s:three,three|fileResourceLineCount=3|tableResource1RecordCount=4|tableResource2RecordCount=4",
        TestUtil.join(out.get(1)));
    Assert.assertEquals("ss2s:four,four|fileResourceLineCount=3|tableResource1RecordCount=4|tableResource2RecordCount=4",
        TestUtil.join(out.get(2)));
  }

}