package cn.fancychuan.scala.stream

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

object RddQueueStreamApp {
    def main(args: Array[String]): Unit = {
        val sparkConf: SparkConf = new SparkConf().setAppName(RddQueueStreamApp.toString).setMaster("local[2]")
        val ssc = new StreamingContext(sparkConf, Seconds(2))
        val sc: SparkContext = ssc.sparkContext

        // 创建一个可变队列
        val queue: mutable.Queue[RDD[Int]] = mutable.Queue[RDD[Int]]()

        val rddDS: InputDStream[Int] = ssc.queueStream(queue, true)
        rddDS.reduce(_ + _).print()

        ssc.start()

        for (item <- 1 to 5) {
            queue += sc.parallelize(item to 100)
            Thread.sleep(2000)
        }

        ssc.awaitTermination()
    }
}
