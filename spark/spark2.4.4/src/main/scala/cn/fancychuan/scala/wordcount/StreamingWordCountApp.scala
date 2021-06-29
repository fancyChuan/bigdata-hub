package cn.fancychuan.scala.wordcount

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}


object StreamingWordCountApp {
    def main(args: Array[String]): Unit = {
        // WARN StreamingContext: spark.master should be set as local[n], n > 1 in local mode
        // if you have receivers to get data, otherwise Spark jobs will not get resources
        // to process the received data.
        val sparkConf: SparkConf = new SparkConf().setAppName("StreamingWordCountApp").setMaster("local[1]")
        // 1. 创建SparkStreaming的入口对象: StreamingContext  参数2: 表示事件间隔   内部会创建 SparkContext
        val ssc = new StreamingContext(sparkConf, Seconds(2))
        // 2. 创建一个DStream
        val lines: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop101", 9999)

        val wordAndOne: DStream[(String, Int)] = lines.flatMap(_.split("\\s+")).map((_, 1))
        val result: DStream[(String, Int)] = wordAndOne.reduceByKey(_ + _)

        println("===========================")
        result.print()
        // 7. 开始接受数据并计算
        ssc.start()
        // 8. 等待计算结束(要么手动退出,要么出现异常)才退出主程序
        ssc.awaitTermination()
    }
}
