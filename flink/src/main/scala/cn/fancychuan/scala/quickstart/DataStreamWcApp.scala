package cn.fancychuan.scala.quickstart

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._

/**
  * 流处理Wordcount应用
  */
object DataStreamWcApp {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    val dataStream: DataStream[String] = env.socketTextStream("s01", 7777)

    val sumdstream: DataStream[(String, Int)] = dataStream.flatMap(_.split(" ")).filter(_.nonEmpty).map((_, 1)).keyBy(0).sum(1)

    sumdstream.print()

    env.execute()
  }
}
