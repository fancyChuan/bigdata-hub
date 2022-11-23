package cn.fancychuan.spark3.sparkcore

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


// 另外一种实现方式
object Spark02_WordCount {
  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local").setAppName("spark3-wordcount")

    val sc = new SparkContext(sparkConf)

    val lines = sc.textFile("spark/data/wordcount.txt")

    val words = lines.flatMap(_.split(" "))

    val wordToOne = words.map(word => (word, 1))

    val wordGroup: RDD[(String, Iterable[(String, Int)])] = wordToOne.groupBy(t => t._1)

    val wordToCount: RDD[(String, Int)] = wordGroup.map {
      case (str, tuples) => {
        tuples.reduce((t1, t2) => (t1._1, t1._2 + t2._2))
      }
    }

    val array: Array[(String, Int)] = wordToCount.collect()
    array.foreach(println)
    sc.stop()
    
  }
}
