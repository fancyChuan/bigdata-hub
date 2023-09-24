package cn.fancychuan.spark3.sparkcore.io

import org.apache.spark.{SparkConf, SparkContext}

object Spark01_RDD_IO_Save {

    def main(args: Array[String]): Unit = {
        val sparConf = new SparkConf().setMaster("local").setAppName("WordCount")
        val sc = new SparkContext(sparConf)

        val rdd = sc.makeRDD(
            List(
                ("a", 1),
                ("b", 2),
                ("c", 3)
            )
        )

        rdd.saveAsTextFile("output1")
        rdd.saveAsObjectFile("output2")
        rdd.saveAsSequenceFile("output3")

        sc.stop()
    }
}
