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

package com.aliyun.odps.spark.examples.streaming.loghub

import com.aliyun.openservices.loghub.client.config.LogHubCursorPosition
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.loghub.{LoghubUtils, StreamingParam}
import org.apache.spark.streaming.{Durations, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object LogHubStreamingDemo {

  def buildParam(conf: SparkConf): StreamingParam = {
    val sp = new StreamingParam()
    sp.setId(conf.get("spark.logservice.accessKeyId"))
    sp.setSecret(conf.get("spark.logservice.accessKeySecret"))
    sp.setEndpoint(conf.get("spark.logservice.endpoint"))
    sp.setProject(conf.get("spark.logservice.project"))
    sp.setLogstore(conf.get("spark.logservice.logstore"))
    sp.setCursor(LogHubCursorPosition.END_CURSOR)
    sp.setGroup("test")
    sp.setLevel(StorageLevel.MEMORY_AND_DISK)

    sp
  }

  def main(args: Array[String]) {
    val conf = new SparkConf(true).setAppName("LogHubStreamingDemo")
    val sc = new SparkContext(conf)

    val ssc = new StreamingContext(sc, Durations.seconds(5))

    val lines = LoghubUtils.createStream(ssc, buildParam(conf), 1).map(line => {
        val str = new String(line)
        str
      })

    val words = lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)

    // Print the first ten elements of each RDD generated in this DStream to the console
    wordCounts.print()

    ssc.start()             // Start the computation
    ssc.awaitTermination()  // Wait for the computation to terminate
  }
}
