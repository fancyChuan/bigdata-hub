package cn.fancychuan.spark3.sparkcore.buildrdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark01_RDD_Memory {

    def main(args: Array[String]): Unit = {

        // TODO 准备环境
        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
        val sc = new SparkContext(sparkConf)

        // TODO 创建RDD
        // 从内存中创建RDD，将内存中集合的数据作为处理的数据源
        val seq = Seq[Int](1,2,3,4)

        // parallelize : 并行
        //val rdd: RDD[Int] = sc.parallelize(seq)
        // makeRDD方法在底层实现时其实就是调用了rdd对象的parallelize方法。
        val rdd: RDD[Int] = sc.makeRDD(seq)

        rdd.collect().foreach(println)

        // TODO 关闭环境
        sc.stop()
    }
}
