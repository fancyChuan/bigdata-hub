package com.aliyun.odps.spark.examples.structuredStreaming.datahub

import org.apache.spark.sql.SparkSession

object DatahubStructuredStreamingDemo {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("DatahubStructuredStreamingDemo")
      .getOrCreate()

    import spark.implicits._

    val df = spark
      .readStream
      .format("datahub")
      .option("datahub.endpoint", "http://dh-cn-beijing.aliyun-inc.com")
      .option("datahub.project", "zkytest")
      .option("datahub.topic", "zkytest")
      .option("datahub.AccessId", "******")
      .option("datahub.AccessKey", "******")
      .option("StartingOffsets", "earliest")
      .load()

    /** *
     * WordCount Demo
     */
    // 请使用OSS作为Checkpoint存储
    val checkpointLocation = "oss://bucket/checkpoint/"
    val lines = df.select($"id").as[String]
    val wordCounts = lines.flatMap(_.split(" ")).toDF("word").groupBy("word").count()

    val query = wordCounts.writeStream
      .outputMode("complete")
      .format("console")
      .option("checkpointLocation", checkpointLocation)
      .start()

    query.awaitTermination()
  }
}

