package cn.fancychuan.spark3.sparkcore.accumulator

import org.apache.spark.{SparkConf, SparkContext}

/**
 * 累加器引入
 */
object Spark01_Acc {

    def main(args: Array[String]): Unit = {

        val sparConf = new SparkConf().setMaster("local").setAppName("Acc")
        val sc = new SparkContext(sparConf)

        val rdd = sc.makeRDD(List(1,2,3,4))

        // reduce : 分区内计算，分区间计算
        //val i: Int = rdd.reduce(_+_)
        //println(i)
        var sum = 0
        rdd.foreach(
            num => {
                sum += num
                // 由于闭包检查，sum是会穿到Executor的，但是累加之后的结果并没有传回Driver
            }
        )
        println("sum = " + sum)

        sc.stop()

    }
}
