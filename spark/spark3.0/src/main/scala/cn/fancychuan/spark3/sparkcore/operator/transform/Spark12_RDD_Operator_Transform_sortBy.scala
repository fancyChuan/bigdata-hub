package cn.fancychuan.spark3.sparkcore.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark12_RDD_Operator_Transform_sortBy {

    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
        val sc = new SparkContext(sparkConf)

        // TODO 算子 - sortBy
        // 排序后分区数不变，中间存在shuffle过程
        val rdd = sc.makeRDD(List(6, 2, 4, 5, 3, 1), 2)

        val newRDD: RDD[Int] = rdd.sortBy(num => num)

        newRDD.saveAsTextFile("output")


        sc.stop()

    }
}
