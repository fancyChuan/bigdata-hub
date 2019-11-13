package cn.fancychuan.scala.apps

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object WordCountApp {
  def main(args: Array[String]): Unit = {
    val config: SparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkWordCount")

    val sc = new SparkContext(config)
    println(sc)

    val lines: RDD[String] = sc.textFile("hadoop/input/wordcount.txt")
    val words: RDD[String] = lines.flatMap(_.split(" "))

    val wordToOne: RDD[(String, Int)] = words.map((word) => (word, 1))

    val wordToSum: RDD[(String, Int)] = wordToOne.reduceByKey(_ + _)

    val result: Array[(String, Int)] = wordToSum.collect()

    print(result)

    result.foreach(println)
  }
}
