package cn.fancychuan.spark3.sparkcore.broadcast

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

/**
 * 问题引入：
 *  用map来代替join操作，性能可以有极大的提高。但如果数据非常大时怎么办？
 *  - 闭包数据，都是以Task为单位发送的，每个任务中都包含了一份闭包数据。
 *  这样会导致一个Executor中包含大量重复数据，占用大量内存
 *  - Executor其实是一个JVM，启动时会自动分配内存，因此可以把闭包数据放到Executor内存中，实现共享
 */
object Spark05_Bc {

    def main(args: Array[String]): Unit = {

        val sparConf = new SparkConf().setMaster("local").setAppName("Acc")
        val sc = new SparkContext(sparConf)

        val rdd1 = sc.makeRDD(List(
            ("a", 1), ("b", 2), ("c", 3)
        ))
        //        val rdd2 = sc.makeRDD(List(
        //            ("a", 4),("b", 5),("c", 6)
        //        ))
        val map = mutable.Map(("a", 4), ("b", 5), ("c", 6))



        // join会导致数据量几何增长，并且会影响shuffle的性能，不推荐使用
        //val joinRDD: RDD[(String, (Int, Int))] = rdd1.join(rdd2)
        //joinRDD.collect().foreach(println)
        // (a, 1),    (b, 2),    (c, 3)
        // (a, (1,4)),(b, (2,5)),(c, (3,6))
        rdd1.map {
            case (w, c) => {
                val l: Int = map.getOrElse(w, 0)
                (w, (c, l))
            }
        }.collect().foreach(println)


        sc.stop()

    }
}
