package cn.fancychuan.spark3.sparkcore.buildrdd

import org.apache.spark.{SparkConf, SparkContext}

object Spark02_RDD_File_Par1 {

    def main(args: Array[String]): Unit = {

        // TODO 准备环境
        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
        val sc = new SparkContext(sparkConf)

        // TODO 创建RDD
        // TODO 数据分区的分配
        // 1. 数据以行为单位进行读取
        //    spark读取文件，采用的是hadoop的方式读取，所以一行一行读取，和字节数没有关系
        // 2. 数据读取时以偏移量为单位,偏移量不会被重复读取
        /*
           1@@   => 012
           2@@   => 345
           3     => 6

         */
        // 3. 数据分区的偏移量范围的计算
        // 0 => [0, 3]  => 12
        // 1 => [3, 6]  => 3
        // 2 => [6, 7]  =>

        // 【1,2】，【3】，【】
        val rdd = sc.textFile("datas/1.txt", 2)

        rdd.saveAsTextFile("output")


        // TODO 关闭环境
        sc.stop()
    }
}
