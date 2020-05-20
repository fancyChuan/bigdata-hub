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
        val rdd1: RDD[Int] = sc.makeRDD(Array(1,2,3,4,5,6,7,8,9,10,12,13,14), 3)
        val rdd2: RDD[Int] = sc.makeRDD(List(1,2,3,4,5,6))

        // 读取文件时，传递的第二个参数是最小分区数，但实际上的分区数不一定是这个，跟hadoop的切片规则有关
        val rdd3: RDD[String] = sc.textFile("spark/data/testTextFile.txt", 2)

        // rdd1.saveAsTextFile("spark/target/test1")

        // map算子
        val mapRDD: RDD[Int] = rdd1.map(x => x * 2) // 也可以写成 rdd1.map(_*2) 因为只用到一个变量x
        // mapPartition算子，减少了发送到执行器的交互次数。但是数据较多的时候容易内容溢出
        val mapPartRDD: RDD[Int] = rdd1.mapPartitions( items => items.map(_*3))
        val mapPartIndexRDD: RDD[String] = rdd1.mapPartitionsWithIndex {
            case (index, items) => {
                items.map(_ + "分区号：" + index)
            }
        }


        mapPartIndexRDD.collect().foreach(println)
    }

}
