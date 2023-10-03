package cn.fancychuan.spark3.sparkcore.practice

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * 将原始数据直接处理成：(品类ID, (点击数量, 0, 0))
 * 然后在map的过程中直接计算，使用flatMap
 */
object Spark03_Req1_HotCategoryTop10Analysis2 {

    def main(args: Array[String]): Unit = {

        // TODO : Top10热门品类
        val sparConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10Analysis")
        val sc = new SparkContext(sparConf)

        // Q : 存在大量的shuffle操作（reduceByKey）
        // reduceByKey 聚合算子，spark会提供优化，缓存

        // 1. 读取原始日志数据
        val actionRDD = sc.textFile("datas/user_visit_action.txt")

        // 2. 将数据转换结构
        //    点击的场合 : ( 品类ID，( 1, 0, 0 ) )
        //    下单的场合 : ( 品类ID，( 0, 1, 0 ) )
        //    支付的场合 : ( 品类ID，( 0, 0, 1 ) )
        val flatRDD: RDD[(String, (Int, Int, Int))] = actionRDD.flatMap(
            action => {
                val datas = action.split("_")
                if (datas(6) != "-1") {
                    // 点击的场合
                    List((datas(6), (1, 0, 0)))
                } else if (datas(8) != "null") {
                    // 下单的场合
                    val ids = datas(8).split(",")
                    ids.map(id => (id, (0, 1, 0)))
                } else if (datas(10) != "null") {
                    // 支付的场合
                    val ids = datas(10).split(",")
                    ids.map(id => (id, (0, 0, 1)))
                } else {
                    Nil
                }
            }
        )

        // 3. 将相同的品类ID的数据进行分组聚合
        //    ( 品类ID，( 点击数量, 下单数量, 支付数量 ) )
        val analysisRDD = flatRDD.reduceByKey(
            (t1, t2) => {
                ( t1._1+t2._1, t1._2 + t2._2, t1._3 + t2._3 )
            }
        )

        // 4. 将统计结果根据数量进行降序处理，取前10名
        val resultRDD = analysisRDD.sortBy(_._2, false).take(10)

        // 5. 将结果采集到控制台打印出来
        resultRDD.foreach(println)

        sc.stop()
    }
}
