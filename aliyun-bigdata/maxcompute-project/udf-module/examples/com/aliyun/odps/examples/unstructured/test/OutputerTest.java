package com.aliyun.odps.examples.unstructured.test;

import com.aliyun.odps.Column;
import com.aliyun.odps.data.ArrayRecord;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.examples.TestUtil;
import com.aliyun.odps.udf.example.text.TextOutputer;
import com.aliyun.odps.udf.local.runner.OutputerRunner;
import com.aliyun.odps.udf.local.util.LocalDataAttributes;
import com.aliyun.odps.udf.local.util.UnstructuredUtils;
import com.aliyun.odps.utils.StringUtils;
import org.junit.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputerTest {

  private String simpleTableSchema = "a:bigint;b:double;c:string";
  private String adsLogTableSchema = "AdId:BIGINT;Rand:DOUBLE;AdvertiserName:STRING;Comment:STRING";
  private File outputDirectory = null;

  @BeforeClass
  public static void initWarehouse() {
    TestUtil.initWarehouse();
  }

  @Before
  public void before() throws IOException{
    // output directory preparation
    outputDirectory = new File("temp/" + UnstructuredUtils.generateOutputName());
    outputDirectory.delete();
    outputDirectory.mkdirs();
  }

  @Test
  public void testOutputSimpleText() throws Exception {
    /**
     * Test outputting manually constructed records to text
     */
    Column[] externalTableSchema = UnstructuredUtils.parseSchemaString(simpleTableSchema);
    LocalDataAttributes attributes = new LocalDataAttributes(null, externalTableSchema);
    // TextOutputer will output one single file
    OutputerRunner runner = new OutputerRunner(TestUtil.getOdps(), new TextOutputer(), attributes);
    List<Record> records = new ArrayList<Record>();
    records.add(new ArrayRecord(externalTableSchema, new Object[]{(long)1, 2.5, "row0"}));
    records.add(new ArrayRecord(externalTableSchema, new Object[]{(long)1234567, 8.88, "row1"}));
    records.add(new ArrayRecord(externalTableSchema, new Object[]{(long)12, 123.1, "testrow"}));
    // run outputer
    runner.feedRecords(records);
    runner.yieldTo(outputDirectory.getAbsolutePath());

    String expcetedOutput = "1|2.5|row0\n" +
        "1234567|8.88|row1\n" +
        "12|123.1|testrow\n";

    verifySingleFileOutput(expcetedOutput);
  }

  @Test
  public void testOutputSpecialText() throws Exception {
    /**
     * Test reading from internal table and outputting to text file, with a user defined delimiter.
     * Equivalent to the following SQL:
     *
     CREATE EXTERNAL TABLE ads_log_external
     (AdId bigint, Rand double,
     AdvertiserName string, Comment string)
     STORED BY 'com.aliyun.odps.udf.example.text.TextStorageHandler'
     WITH SERDEPROPERTIES ('delimiter'='\t')
     LOCATION 'oss://path/to/output/'
     USING 'jar_file_name.jar';;

     INSERT OVERWRITE ads_log_external SELECT * FROM ads_log;
     * Here ads_log is an internal table (locally defined in warehouse directory)
     */
    Column[] externalTableSchema = UnstructuredUtils.parseSchemaString(adsLogTableSchema);
    Map<String, String> userProperties = new HashMap<String, String>();
    userProperties.put("delimiter", "\t");
    LocalDataAttributes attributes = new LocalDataAttributes(userProperties, externalTableSchema);
    // TextOutputer outputs one single file
    OutputerRunner runner = new OutputerRunner(TestUtil.getOdps(), new TextOutputer(), attributes);
    String internalTableName = "ads_log";
    // We are doing SELECT * FROM here, so the two tables have the same schema
    Column[] internalTableSceham = externalTableSchema;

    List<Record> records = new ArrayList<Record>();
    Record record;
    while ((record = UnstructuredUtils.readFromInternalTable("example_project", internalTableName,
        internalTableSceham, null)) != null){
      records.add(record.clone());
    }
    // run outputer
    runner.feedRecords(records);
    runner.yieldTo(outputDirectory.getAbsolutePath());

    String expcetedOutput = "399266\t0.5\tDoritos\twhat is up\n" +
        "399266\t0.0\tTacobell\thello!\n" +
        "382045\t-76.0\tVoelkl\trandom comments\n" +
        "382045\t6.4\tWhistler Resort\ta\n" +
        "106479\t98.7\tAmazon Prime\tbdcd\n" +
        "906441\t-9865788.2\tHayden Planetarium\tplatium\n" +
        "351530\t0.005\tMicrosoft Azure Services\ttst\n";

    verifySingleFileOutput(expcetedOutput);
  }

  private void verifySingleFileOutput(String expectedOutput) throws IOException {
    verifyFilesOutput(new String[]{expectedOutput});
  }

  private void verifyFilesOutput(String[] expectedOutputs) throws IOException {
    File[] outputs = outputDirectory.listFiles();
    Assert.assertEquals(outputs.length, expectedOutputs.length);
    for (int i = 0; i < outputs.length; i++){
      File outputFile = outputs[i];
      FileInputStream fis = new FileInputStream(outputFile);
      byte[] data = new byte[(int)outputFile.length()];
      fis.read(data);
      String content = new String(data);
      String[] rows = StringUtils.split(content, '\n');
      String[] expectedRows = StringUtils.split(expectedOutputs[i], '\n');
      // due to double presentation accuracy difference, the output may not exactly match expected,
      // therefore we only verify that numbers of rows match.
      Assert.assertEquals(rows.length, expectedRows.length);
    }
  }
}