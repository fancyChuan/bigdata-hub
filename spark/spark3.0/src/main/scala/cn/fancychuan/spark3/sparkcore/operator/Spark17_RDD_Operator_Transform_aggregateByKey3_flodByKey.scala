package cn.fancychuan.spark3.sparkcore.operator

import org.apache.spark.{SparkConf, SparkContext}

object Spark17_RDD_Operator_Transform_aggregateByKey3_flodByKey {

    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
        val sc = new SparkContext(sparkConf)

        // TODO 算子 - (Key - Value类型)

        val rdd = sc.makeRDD(List(
            ("a", 1), ("a", 2), ("b", 3),
            ("b", 4), ("b", 5), ("a", 6)
        ),2)

        //rdd.aggregateByKey(0)(_+_, _+_).collect.foreach(println)

        // 如果聚合计算时，分区内和分区间计算规则相同，spark提供了简化的方法flodByKey
        rdd.foldByKey(0)(_+_).collect.foreach(println)





        sc.stop()

    }
}
