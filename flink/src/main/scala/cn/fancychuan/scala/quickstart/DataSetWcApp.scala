package cn.fancychuan.scala.quickstart

import org.apache.flink.api.scala.{AggregateDataSet, DataSet, ExecutionEnvironment, _}

/**
  * 批处理Wordcount应用
  *
  * flink应用的4个步骤：
  * 1. 创建环境env
  * 2. 配置数据源source
  * 3. 数据转换transform
  * 4. 结果输出sink
  */
object DataSetWcApp {
  def main(args: Array[String]): Unit = {

    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    val txtDataSet: DataSet[String] = env.readTextFile("hadoop/input/wordcount.txt")

    val aggSet: AggregateDataSet[(String, Int)] = txtDataSet.flatMap(_.split(" ")).map((_, 1)).groupBy(0).sum(1)

    aggSet.print()
  }
}
