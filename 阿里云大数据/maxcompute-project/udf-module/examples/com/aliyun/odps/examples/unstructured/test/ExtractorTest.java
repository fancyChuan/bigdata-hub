package com.aliyun.odps.examples.unstructured.test;

import com.aliyun.odps.Column;
import com.aliyun.odps.data.ArrayRecord;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.examples.TestUtil;
import com.aliyun.odps.examples.unstructured.SpeechSentenceSnrExtractor;
import com.aliyun.odps.examples.unstructured.TextExtractor;
import com.aliyun.odps.udf.local.runner.ExtractorRunner;
import com.aliyun.odps.udf.local.util.LocalDataAttributes;
import com.aliyun.odps.udf.local.util.UnstructuredUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtractorTest {
  private String ambulanceFullSchema =
      "vehicle:bigint;id:bigint;patient:bigint;calls:bigint;latitude:double;longitude:double;time:string;direction:string";
  private String speechDataFullSchema = "sentence_snr:double;id:string";

  @BeforeClass
  public static void initWarehouse() {
    TestUtil.initWarehouse();
  }

  @Test
  public void testTextExtractor() throws Exception {
    /**
     * Equivalent to the following SQL:
     CREATE EXTERNAL TABLE  ambulance_data_external
     ( vehicle bigint, id bigint, patient bigint, calls bigint,
     Latitude double, Longitude double, time string, direction string)
     STORED BY 'com.aliyun.odps.udf.example.text.TextStorageHandler'
     LOCATION 'oss://.../data/ambulance_csv/'
     USING 'jar_file_name.jar';

     SELECT * FROM ambulance_data_external;
     */
    Column[] externalTableSchema = UnstructuredUtils.parseSchemaString(ambulanceFullSchema);
    // note: default delimiter used in TextExtractor is ','
    LocalDataAttributes attributes = new LocalDataAttributes(null, externalTableSchema);
    ExtractorRunner runner = new ExtractorRunner(TestUtil.getOdps(), new TextExtractor(), attributes);
    //using local file directory to mock data source
    runner.feedDirectory(TestUtil.class.getResource("/data/ambulance_csv/").getPath());
    List<Record> records = runner.yieldRecords();
    // do verification below
    Assert.assertEquals(records.size(), 15);
    ArrayRecord record0 = new ArrayRecord(externalTableSchema);
    record0.set(0, (long)1);
    record0.set(1, (long)1);
    record0.set(2, (long)51);
    record0.set(3, (long)1);
    record0.set(4, 46.81006);
    record0.set(5, -92.08174);
    record0.set(6, "9/14/2014 0:00");
    record0.set(7, "S");
    Assert.assertTrue(UnstructuredUtils.recordsEqual(record0, records.get(0)));
  }

  @Test
  public void testSpeechExtraction() throws Exception {
    /**
     * Equivalent to the following SQL:
     CREATE EXTERNAL TABLE speech_snr_external
     (sentence_snr double, id string)
     STORED BY 'com.aliyun.odps.udf.example.speech.SpeechStorageHandler'
     WITH SERDEPROPERTIES ('mlfFileName'='speech_model_random_5_utterance' , 'speechSampleRateInKHz' = '16')
     LOCATION 'oss://.../data/speech_wav/'
     USING 'jar_file_name.jar';

     SELECT * FROM speech_snr_external;
     */
    Column[] externalTableSchema = UnstructuredUtils.parseSchemaString(speechDataFullSchema);
    Map<String, String> userProperties = new HashMap<String, String>();
    // a file resource
    userProperties.put("mlfFileName", "speech_model_random_5_utterance");
    // an extractor parameter
    userProperties.put("speechSampleRateInKHz", "16");
    LocalDataAttributes attributes = new LocalDataAttributes(userProperties, externalTableSchema);
    // SpeechSentenceSnrExtractor will analyze a speech wav file and output
    // 1. the average sentence snr of a wav file
    // 2. the corresponding wav file name
    ExtractorRunner runner = new ExtractorRunner(TestUtil.getOdps(), new SpeechSentenceSnrExtractor(), attributes);

    runner.feedDirectory(TestUtil.class.getResource("/data/speech_wav/").getPath());
    List<Record> records = runner.yieldRecords();

    // do verification below
    Assert.assertEquals(records.size(), 3);

    ArrayRecord record0 = new ArrayRecord(externalTableSchema);
    record0.set(0, 31.39050062838079);
    record0.set(1, "tsh148_seg_2_3013_3_6_48_80bd359827e24dd7_0");
    Assert.assertTrue(UnstructuredUtils.recordsEqual(record0, records.get(0)));

    ArrayRecord record1 = new ArrayRecord(externalTableSchema);
    record1.set(0, 35.477360745366035);
    record1.set(1, "tsh148_seg_3013_1_31_11_9d7c87aef9f3e559_0");
    Assert.assertTrue(UnstructuredUtils.recordsEqual(record1, records.get(1)));

    ArrayRecord record2 = new ArrayRecord(externalTableSchema);
    record2.set(0, 16.046150955268665);
    record2.set(1, "tsh148_seg_3013_2_29_49_f4cb0990a6b4060c_0");
    Assert.assertTrue(UnstructuredUtils.recordsEqual(record2, records.get(2)));
  }

}