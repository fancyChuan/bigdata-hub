package cn.fancychuan.spark3.sparkcore.operator

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark06_RDD_Operator_Transform_groupBy_Test {

    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
        val sc = new SparkContext(sparkConf)

        // TODO 算子 - groupBy
        // 实现功能：获取每个时间段访问量
        val rdd = sc.textFile("datas/apache.log")

        val timeRDD: RDD[(String, Iterable[(String, Int)])] = rdd.map(
            line => {
                val datas = line.split(" ")
                val time = datas(3)
                //time.substring(0, )
                val sdf = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss")
                val date: Date = sdf.parse(time)
                val sdf1 = new SimpleDateFormat("HH")
                val hour: String = sdf1.format(date)
                (hour, 1)
            }
        ).groupBy(_._1)
        timeRDD.map{
            case ( hour, iter ) => {
                (hour, iter.size)
            }
        }.collect.foreach(println)


        sc.stop()

    }
}
