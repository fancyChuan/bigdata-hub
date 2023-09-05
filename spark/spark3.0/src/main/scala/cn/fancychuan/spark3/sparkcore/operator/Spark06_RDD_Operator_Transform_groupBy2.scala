package cn.fancychuan.spark3.sparkcore.operator

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark06_RDD_Operator_Transform_groupBy2 {

    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
        val sc = new SparkContext(sparkConf)

        // TODO 算子 - groupBy
        val rdd  = sc.makeRDD(List("Hello", "Spark", "Scala", "Hadoop"), 2)

        // 分组和分区没有必然的关系
        val groupRDD = rdd.groupBy(_.charAt(0)) // 根据首写字母来分组

        groupRDD.collect().foreach(println)


        sc.stop()

    }
}
