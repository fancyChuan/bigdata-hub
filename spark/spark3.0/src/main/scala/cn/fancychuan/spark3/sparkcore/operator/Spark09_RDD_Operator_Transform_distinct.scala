package cn.fancychuan.spark3.sparkcore.operator

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark09_RDD_Operator_Transform_distinct {

    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
        val sc = new SparkContext(sparkConf)

        // TODO 算子 - distinct 去重
        val rdd = sc.makeRDD(List(1,2,3,4,1,2,3,4))


        // 源码中去重的实现代码：map(x => (x, null)).reduceByKey((x, _) => x, numPartitions).map(_._1)
        // reduceByKey的时候，将相同的key汇总，同时将value聚合；而聚合的逻辑是取第1个null
        // 最后再取key返回，就达到了去重的目的
        // (1, null),(2, null),(3, null),(4, null),(1, null),(2, null),(3, null),(4, null)
        // (1, null)(1, null)(1, null)
        // (null, null) => null
        // (1, null) => 1
        val rdd1: RDD[Int] = rdd.distinct()

        rdd1.collect().foreach(println)



        sc.stop()

    }
}
