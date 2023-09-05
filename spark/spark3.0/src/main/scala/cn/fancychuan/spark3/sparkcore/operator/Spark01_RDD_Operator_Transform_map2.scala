package cn.fancychuan.spark3.sparkcore.operator

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark01_RDD_Operator_Transform_map2 {

    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
        val sc = new SparkContext(sparkConf)

        // TODO 算子 - map
        val rdd = sc.makeRDD(List(1,2,3,4),2)
        // 【1，2】，【3，4】
        rdd.saveAsTextFile("output")
        val mapRDD = rdd.map(_*2)
        // 【2，4】，【6，8】
        mapRDD.saveAsTextFile("output1")

        sc.stop()

    }
}
