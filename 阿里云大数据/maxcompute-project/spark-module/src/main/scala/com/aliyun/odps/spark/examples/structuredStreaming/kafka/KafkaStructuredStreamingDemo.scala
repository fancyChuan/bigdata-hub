package com.aliyun.odps.spark.examples.structuredStreaming.kafka

import java.sql.Timestamp

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.window

object KafkaStructuredStreamingDemo{
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("KafkaStreamingDemo")
      .getOrCreate()

    import spark.implicits._

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "topic")
      .load()

    /** *
     * WordCount Demo
     */
    // 请使用OSS作为Checkpoint存储
    val checkpointLocation = "oss://bucket/checkpoint/"
    val lines = df.selectExpr("cast(value as string)").as[String]
    val wordCounts = lines.flatMap(_.split(" ")).toDF("word").groupBy("word").count()

    val query = wordCounts.writeStream
      .outputMode("complete")
      .format("console")
      .option("checkpointLocation", checkpointLocation)
      .option("path", "query1")
      .start()

    query.awaitTermination()

    /** *
     * Windowed WordCount Demo
     */
    val wordsWithTimestamp = df.selectExpr("cast(value as string)").as[String]
      .flatMap(x => {
        val Array(ts, data) = x.split(",")
        data.split(" ").map((new Timestamp(ts.toLong), _))
      }).as[(Timestamp, String)].toDF("timestamp", "word")

    // 请使用OSS作为Checkpoint存储
    val checkpointLocation2 = "oss://bucket/checkpoint2/"
    val windowedCounts = wordsWithTimestamp
      .groupBy(
        window($"timestamp", "10 seconds", "5 seconds"),
        $"word"
      ).count()

    val query2 = windowedCounts.writeStream
      .outputMode("complete")
      .format("console")
      .option("checkpointLocation", checkpointLocation2)
      .start()

    query2.awaitTermination()

    /** *
     * Windowed WordCount with Watermark Demo
     */
    // 请使用OSS作为Checkpoint存储
    val checkpointLocation3 = "oss://bucket/checkpoint3/"

    val windowedCountsWithWatermark = wordsWithTimestamp
      .withWatermark("timestamp", "5 seconds")
      .groupBy(
        window($"timestamp", "6 seconds", "3 seconds"),
        $"word"
      ).count()

    val query3 = windowedCountsWithWatermark.writeStream
      .outputMode("append")
      .format("console")
      .option("checkpointLocation", checkpointLocation3)
      .start()

    query3.awaitTermination()
  }
}

