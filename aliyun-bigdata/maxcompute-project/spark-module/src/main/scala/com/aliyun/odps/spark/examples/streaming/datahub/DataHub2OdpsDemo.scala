package com.aliyun.odps.spark.examples.streaming.datahub

import com.aliyun.datahub.model.RecordEntry
import com.aliyun.odps.spark.examples.streaming.common.SparkSessionSingleton

import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.aliyun.datahub.DatahubUtils

object DataHub2OdpsDemo {

  def transferFunc(record: RecordEntry): String = {
    // 这个转化函数目前只支持把DataHub Record转成String
    // 如果是需要多个字段的话, 那么需要处理一下拼接的逻辑
    record.getString(1)
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("DataHubStreamingDemo")
      .config("spark.hadoop.fs.oss.credentials.provider", "org.apache.hadoop.fs.aliyun.oss.AliyunStsTokenCredentialsProvider")
      .config("spark.hadoop.fs.oss.ststoken.roleArn", "acs:ram::****:role/aliyunodpsdefaultrole")
      .config("spark.hadoop.fs.oss.endpoint", "oss-cn-hangzhou-zmf.aliyuncs.com")
      .getOrCreate()

    // 设置Batch间隔时间
    val ssc = new StreamingContext(spark.sparkContext, Seconds(10))

    // checkpoint dir to oss
    ssc.checkpoint("oss://bucket/inputdata/")

    val dataStream = DatahubUtils.createStream(
      ssc,
      "projectName",
      "topic",
      "subId",
      "accessId",
      "accessKey",
      "endPoint",
      transferFunc(_),
      StorageLevel.MEMORY_AND_DISK
    )

    dataStream.map(x => new String(x)).foreachRDD(rdd => {
      val spark = SparkSessionSingleton.getInstance(rdd.sparkContext.getConf)
      import spark.implicits._

      rdd.toDF("id").write.mode("append").saveAsTable("test_table")
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
