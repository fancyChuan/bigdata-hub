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

import com.aliyun.odps.spark.examples.streaming.common.SparkSessionSingleton
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

object Kafka2OdpsDemo {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("test")
    val ssc = new StreamingContext(sparkConf, Seconds(10))

    // 请使用OSS作为Checkpoint存储,修改为有效OSS路径。OSS访问文档请参考 https://github.com/aliyun/MaxCompute-Spark/wiki/08.-Oss-Access%E6%96%87%E6%A1%A3%E8%AF%B4%E6%98%8E
    ssc.checkpoint("oss://bucket/checkpointdir")

    // kafka配置参数
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "testGroupId",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    // 创建kafka dstream
    val topics = Set("test")
    val recordDstream: InputDStream[ConsumerRecord[String, String]] =
      KafkaUtils.createDirectStream[String, String](
        ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
      )
    val dstream = recordDstream.map(f => (f.key(), f.value()))
    // 解析kafka数据并写入odps
    val data: DStream[String] = dstream.map(_._2)
    val wordsDStream: DStream[String] = data.flatMap(_.split(" "))
    wordsDStream.foreachRDD(rdd => {
      val spark = SparkSessionSingleton.getInstance(rdd.sparkContext.getConf)
      import spark.implicits._

      rdd.toDF("id").write.mode("append").saveAsTable("test_table")
    })

    ssc.start()
    ssc.awaitTermination()
  }
}

