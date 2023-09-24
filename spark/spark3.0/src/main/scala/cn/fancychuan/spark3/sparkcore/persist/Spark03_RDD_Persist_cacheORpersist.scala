package cn.fancychuan.spark3.sparkcore.persist

import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

/**
 * 持久化操作
 */
object Spark03_RDD_Persist_cacheORpersist {

    def main(args: Array[String]): Unit = {
        val sparConf = new SparkConf().setMaster("local").setAppName("Persist")
        val sc = new SparkContext(sparConf)

        val list = List("Hello Scala", "Hello Spark")

        val rdd = sc.makeRDD(list)

        val flatRDD = rdd.flatMap(_.split(" "))

        val mapRDD = flatRDD.map(word=>{
            println("@@@@@@@@@@@@")
            (word,1)
        })
        // cache默认持久化的操作，只能将数据保存到内存中，如果想要保存到磁盘文件，需要更改存储级别
        //mapRDD.cache()

        // 持久化操作必须在行动算子执行时完成的。
        mapRDD.persist(StorageLevel.DISK_ONLY)

        val reduceRDD: RDD[(String, Int)] = mapRDD.reduceByKey(_+_)
        reduceRDD.collect().foreach(println)
        println("**************************************")
        val groupRDD = mapRDD.groupByKey()
        groupRDD.collect().foreach(println)


        sc.stop()
    }
}
