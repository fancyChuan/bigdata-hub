package cn.fancychuan.spark3.sparkcore

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


// 另外一种实现方式，spark提供了更强大的函数
object Spark03_WordCount {
  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local").setAppName("spark3-wordcount")

    val sc = new SparkContext(sparkConf)

    val lines = sc.textFile("spark/data/wordcount.txt")

    val words = lines.flatMap(_.split(" "))

    val wordToOne = words.map(word => (word, 1))

    val wordToCount: RDD[(String, Int)] = wordToOne.reduceByKey(_ + _) //

    val array: Array[(String, Int)] = wordToCount.collect()
    array.foreach(println)
    sc.stop()
    
  }
}
