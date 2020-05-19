package cn.fancychuan.scala.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * spark core
  * RDD编程
  */
object SparkCoreApp {

    def main(args: Array[String]): Unit = {

        val conf = new SparkConf().setMaster("local[*]").setAppName("SparkCoreRDD")
        val sc = new SparkContext(conf)

        // makeRDD的底层实现就是parallelize，传入的第一个参数要求是Seq需要有顺序，那么Array和List都可以
        val rdd1: RDD[Int] = sc.makeRDD(Array(1,2,3,4,5,6,7,8,9,10,12,13,14))
        val rdd2: RDD[Int] = sc.makeRDD(List(1,2,3,4,5,6))

        // 读取文件时，传递的第二个参数是最小分区数，但实际上的分区数不一定是这个，跟hadoop的切片规则有关
        val rdd3: RDD[String] = sc.textFile("spark/data/testTextFile.txt", 2)

        // rdd1.saveAsTextFile("spark/target/test1")

        rdd3.foreach(println)
    }

}
