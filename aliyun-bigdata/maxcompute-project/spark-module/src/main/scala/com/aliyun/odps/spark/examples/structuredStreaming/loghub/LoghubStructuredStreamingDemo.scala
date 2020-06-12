package com.aliyun.odps.spark.examples.structuredStreaming.loghub

import org.apache.spark.sql.SparkSession

object LoghubStructuredStreamingDemo {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("LoghubStructuredStreamingDemo")
      .getOrCreate()

    import spark.implicits._

    val df = spark
      .readStream
      .format("loghub")
      .option("Loghub.Endpoint", "cn-beijing-intranet.log.aliyuncs.com")
      .option("Loghub.Project", "zkytest")
      .option("Loghub.AccessId", "******")
      .option("Loghub.AccessKey", "******")
      .option("Loghub.Logstores", "zkytest")
      .option("StartingOffsets", "latest")
      .load()

    /** *
     * WordCount Demo
     */
    // 请使用OSS作为Checkpoint存储
    val checkpointLocation = "oss://bucket/checkpoint"
    val lines = df.select($"contents").as[String]
    val wordCounts = lines.flatMap(_.split(" ")).toDF("word").groupBy("word").count()

    val query = wordCounts.writeStream
      .outputMode("complete")
      .format("console")
      .option("checkpointLocation", checkpointLocation)
      .start()

    query.awaitTermination()
  }
}

