package cn.fancychuan.spark3.sparkcore.operator.transform

import org.apache.spark.{SparkConf, SparkContext}

object Spark01_RDD_Operator_Transform_map2 {

    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
        val sc = new SparkContext(sparkConf)

        // TODO 算子 - map 演示分区不变（转化操作之后分区数量不变）
        val rdd = sc.makeRDD(List(1, 2, 3, 4), 2)
        // 分区效果【1，2】，【3，4】
        rdd.saveAsTextFile("output")
        val mapRDD = rdd.map(_ * 2)
        // 分区效果【2，4】，【6，8】
        mapRDD.saveAsTextFile("output1")

        sc.stop()

    }
}
