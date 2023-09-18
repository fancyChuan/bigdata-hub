package cn.fancychuan.spark3.sparkcore.operator.transform

import org.apache.spark.{SparkConf, SparkContext}

object Spark05_RDD_Operator_Transform_glom {

    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
        val sc = new SparkContext(sparkConf)

        // TODO 算子 - glom 将同一个分区的数据直接转换为相同类型的内存数组进行处理，分区不变
        val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)

        // List => Int
        // Int => Array
        val glomRDD: RDD[Array[Int]] = rdd.glom()

        glomRDD.collect().foreach(data => println(data.mkString(",")))


        sc.stop()

    }
}
