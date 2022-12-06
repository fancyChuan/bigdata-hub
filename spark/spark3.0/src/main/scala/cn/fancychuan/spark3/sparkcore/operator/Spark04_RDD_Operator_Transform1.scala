package cn.fancychuan.spark3.sparkcore.operator

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark04_RDD_Operator_Transform1 {

    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
        val sc = new SparkContext(sparkConf)

        // TODO 算子 - flatMap
        val rdd: RDD[String] = sc.makeRDD(List(
            "Hello Scala", "Hello Spark"
        ))

        val flatRDD: RDD[String] = rdd.flatMap(
            s => {
                s.split(" ")
            }
        )
        flatRDD.collect().foreach(println)



        sc.stop()

    }
}
