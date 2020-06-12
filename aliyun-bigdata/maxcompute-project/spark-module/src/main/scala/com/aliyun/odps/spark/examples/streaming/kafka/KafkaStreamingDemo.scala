/**
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  * <p>
  * http://www.apache.org/licenses/LICENSE-2.0
  * <p>
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package com.aliyun.odps.spark.examples.streaming.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaStreamingDemo {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("KafkaStreamingDemo")
      .getOrCreate()

    val ssc = new StreamingContext(spark.sparkContext, Seconds(5))

    // 请使用OSS作为Checkpoint存储
    ssc.checkpoint("oss://bucket/checkpointDir/")

    // kafka配置参数
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "192.168.1.1:9200,192.168.1.2:9200,192.168.1.3:9200",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "testGroupId",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Set("event_topic")
    val recordDstream: InputDStream[ConsumerRecord[String, String]] =
      KafkaUtils.createDirectStream[String, String](
        ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
      )


    val dstream = recordDstream.map(f => (f.key(), f.value()))
    val data: DStream[String] = dstream.map(_._2)
    val wordsDStream: DStream[String] = data.flatMap(_.split(" "))
    val wordAndOneDstream: DStream[(String, Int)] = wordsDStream.map((_, 1))
    val result: DStream[(String, Int)] = wordAndOneDstream.reduceByKey(_ + _)
    result.print()

    ssc.start()
    ssc.awaitTermination()
  }
}

