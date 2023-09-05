package cn.fancychuan.spark3.sparkcore.operator

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark07_RDD_Operator_Transform_filter_Test {

    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
        val sc = new SparkContext(sparkConf)

        // TODO 算子 - filter
        // 实现功能：获取 2015 年 5 月 17 日的请求路径
        val rdd = sc.textFile("datas/apache.log")

        rdd.filter(
            line => {
                val datas = line.split(" ")
                val time = datas(3)
                time.startsWith("17/05/2015")
            }
        ).collect().foreach(println)


        sc.stop()

    }
}
