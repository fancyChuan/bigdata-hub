package cn.fancychuan.spark3.sparkcore

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * 使用Kryo序列化框架的示例
 * Spark2.0开始已经使用了该序列。
 * 当 RDD 在 Shuffle 数据的时候，简单数据类型、数组和字符串类型已经在 Spark 内部使用 Kryo 来序列化
 */
object serializable_Kryo {
    def main(args: Array[String]): Unit = { val conf: SparkConf = new SparkConf()
      .setAppName("SerDemo")
      .setMaster("local[*]")
      // 替换默认的序列化机制
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      // 注册需要使用 kryo 序列化的自定义类
      .registerKryoClasses(Array(classOf[Searcher])) val sc = new SparkContext(conf)
        val rdd: RDD[String] = sc.makeRDD(Array("hello world", "hello atguigu", "atguigu", "hahah"), 2)

        val searcher = new Searcher("hello")
        val result: RDD[String] = searcher.getMatchedRDD1(rdd)

        result.collect.foreach(println)
    }
}
case class Searcher(val query: String) {

    def isMatch(s: String) = {
        s.contains(query)
    }

    def getMatchedRDD1(rdd: RDD[String]) = {
        rdd.filter(isMatch)
    }

    def getMatchedRDD2(rdd: RDD[String]) = {
        val q = query
        rdd.filter(_.contains(q))
    }
}
