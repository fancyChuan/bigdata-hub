package cn.fancychuan.spark3.sparkcore.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

object Spark14_RDD_Operator_Transform_partitionBy {

    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
        val sc = new SparkContext(sparkConf)

        // TODO 算子 - (Key - Value类型)
        val rdd = sc.makeRDD(List(1, 2, 3, 4), 2)

        val mapRDD: RDD[(Int, Int)] = rdd.map((_, 1))
        // RDD => PairRDDFunctions
        // 隐式转换（二次编译）

        // partitionBy根据指定的分区规则对数据进行重分区
        val newRDD = mapRDD.partitionBy(new HashPartitioner(2))

        // 传了同一个分区器，源码上会把传递的分区器和自己的分区器比较，如果一致就什么都不做
        newRDD.partitionBy(new HashPartitioner(2))

        newRDD.saveAsTextFile("output")


        sc.stop()

    }
}
