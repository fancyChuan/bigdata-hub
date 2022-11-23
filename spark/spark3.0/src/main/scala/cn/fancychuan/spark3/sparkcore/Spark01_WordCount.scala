package cn.fancychuan.spark3.sparkcore

import org.apache.spark.{SparkConf, SparkContext}

object Spark01_WordCount {
  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local").setAppName("spark3-wordcount")

    val sc = new SparkContext(sparkConf)

    val lines = sc.textFile("spark/data/wordcount.txt")

    val words = lines.flatMap(_.split(" "))

    val wordGroup = words.groupBy(word => word)

    val wordToCount = wordGroup.map {
      case (word, list) => {
        (word, list.size)
      }
    }

    val array: Array[(String, Int)] = wordToCount.collect()
    array.foreach(println)
    sc.stop()
    
  }
}
