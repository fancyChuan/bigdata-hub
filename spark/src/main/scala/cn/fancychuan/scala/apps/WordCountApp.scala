package cn.fancychuan.scala.apps

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object WordCountApp {
  def main(args: Array[String]): Unit = {
    var args1: Array[String] = new Array[String](2)
    if (args.length == 0) {
      args1 = Array("hadoop/input/wordcount.txt", "spark/target/wordcount")
      println(args1)
    } else {
      args1 = args
    }
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkWordCount")

    val sc = new SparkContext(config)
    println(sc)

    // 这里地方路径跟spark部署方式有关。比如配置的是yarn，那么会读取HDFS上的路径
    // 如果我们需要本地文件系统，那么要写成 file:///opt/module/xxxxx
    val lines: RDD[String] = sc.textFile(args1(0))
    val words: RDD[String] = lines.flatMap(_.split(" "))

    val wordToOne: RDD[(String, Int)] = words.map((word) => (word, 1))

    val wordToSum: RDD[(String, Int)] = wordToOne.reduceByKey(_ + _)

    val result: Array[(String, Int)] = wordToSum.collect()

    print(result)
    result.foreach(println)

    wordToSum.saveAsTextFile(args1(1))
  }
}
