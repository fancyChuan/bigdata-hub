package cn.fancychuan.spark3.sparkcore.operator.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark01_RDD_Operator_Transform {

    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
        val sc = new SparkContext(sparkConf)

        // TODO 算子 - map

        val rdd = sc.makeRDD(List(1, 2, 3, 4))
        // 1,2,3,4
        // 2,4,6,8

        // 转换函数
        def mapFunction(num: Int): Int = {
            num * 2
        }

        //val mapRDD: RDD[Int] = rdd.map(mapFunction)
        //val mapRDD: RDD[Int] = rdd.map((num:Int)=>{num*2})
        //val mapRDD: RDD[Int] = rdd.map((num:Int)=>num*2) // 函数只有一行，大括号可以省略
        //val mapRDD: RDD[Int] = rdd.map((num)=>num*2) // 类型可以推断出来，那么Int也可以省略
        //val mapRDD: RDD[Int] = rdd.map(num=>num*2) // 参数只有一个，括号可以省略
        val mapRDD: RDD[Int] = rdd.map(_ * 2) // 变量只出现一次，且按顺序，可以用_代替


        mapRDD.collect().foreach(println)

        sc.stop()

    }
}
