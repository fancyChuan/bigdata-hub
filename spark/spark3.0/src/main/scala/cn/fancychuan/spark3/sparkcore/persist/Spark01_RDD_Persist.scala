package cn.fancychuan.spark3.sparkcore.persist

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * 写两遍wc的代码，用不同的方法实现
 */
object Spark01_RDD_Persist {

    def main(args: Array[String]): Unit = {
        val sparConf = new SparkConf().setMaster("local").setAppName("WordCount")
        val sc = new SparkContext(sparConf)

        val list = List("Hello Scala", "Hello Spark")

        val rdd = sc.makeRDD(list)

        val flatRDD = rdd.flatMap(_.split(" "))

        val mapRDD = flatRDD.map((_,1))

        val reduceRDD: RDD[(String, Int)] = mapRDD.reduceByKey(_+_)

        reduceRDD.collect().foreach(println)
        println("**************************************")

        val rdd1 = sc.makeRDD(list)

        val flatRDD1 = rdd1.flatMap(_.split(" "))

        val mapRDD1 = flatRDD1.map((_,1))

        val groupRDD = mapRDD1.groupByKey()

        groupRDD.collect().foreach(println)


        sc.stop()
    }
}
