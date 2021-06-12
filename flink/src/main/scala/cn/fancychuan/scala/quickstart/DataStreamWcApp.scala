package cn.fancychuan.scala.quickstart

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._

/**
  * 流处理Wordcount应用
  *开发流程：
  *   1. 初始化流计算的环境
  *   2. 导入隐式转换
  *   3. 读取数据，读取sock流中的数据
  *   4. 转换和处理数据
  *   5. 打印结果
  *   6. 启动流计算程序
  */
object DataStreamWcApp {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.setParallelism(3) // 设置并行度为3

    // 使用socket文本流
    val dataStream: DataStream[String] = env.socketTextStream("hadoop101", 7777)

    val sumdstream: DataStream[(String, Int)] = dataStream.flatMap(_.split(" "))
      .filter(_.nonEmpty)
      .map((_, 1))
      .keyBy(0) // 分组算子 0,1代表下标
      .sum(1) // 按第2个元素sum

    sumdstream.print()

    env.execute("流处理wordcount作业")
  }
}
