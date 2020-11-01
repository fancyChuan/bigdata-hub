package cn.fancychuan.scala.wordcount

import org.apache.spark.{SparkConf, SparkContext}

object WordcountApp {
  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("wordcount-scala").setMaster("local")
    val sc = new SparkContext(sparkConf)



  }
}
