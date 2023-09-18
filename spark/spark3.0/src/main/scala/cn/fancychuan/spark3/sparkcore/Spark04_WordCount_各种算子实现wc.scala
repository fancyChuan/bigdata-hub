package cn.fancychuan.spark3.sparkcore

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

object Spark04_WordCount_各种算子实现wc {
    def main(args: Array[String]): Unit = {

        val sparConf = new SparkConf().setMaster("local").setAppName("WordCount")
        val sc = new SparkContext(sparConf)

        wordcount91011(sc)

        sc.stop()

    }

    // groupBy
    def wordcount1(sc : SparkContext): Unit = {

        val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
        val words = rdd.flatMap(_.split(" "))
        val group: RDD[(String, Iterable[String])] = words.groupBy(word=>word)
        val wordCount: RDD[(String, Int)] = group.mapValues(iter=>iter.size)
    }

    // groupByKey
    def wordcount2(sc : SparkContext): Unit = {
        val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
        val words = rdd.flatMap(_.split(" "))
        val wordOne = words.map((_,1))
        val group: RDD[(String, Iterable[Int])] = wordOne.groupByKey()
        val wordCount: RDD[(String, Int)] = group.mapValues(iter=>iter.size)
    }

    // reduceByKey
    def wordcount3(sc : SparkContext): Unit = {
        val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
        val words = rdd.flatMap(_.split(" "))
        val wordOne = words.map((_,1))
        val wordCount: RDD[(String, Int)] = wordOne.reduceByKey(_+_)
    }

    // aggregateByKey
    def wordcount4(sc : SparkContext): Unit = {
        val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
        val words = rdd.flatMap(_.split(" "))
        val wordOne = words.map((_,1))
        val wordCount: RDD[(String, Int)] = wordOne.aggregateByKey(0)(_+_, _+_)
    }

    // foldByKey
    def wordcount5(sc : SparkContext): Unit = {
        val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
        val words = rdd.flatMap(_.split(" "))
        val wordOne = words.map((_,1))
        val wordCount: RDD[(String, Int)] = wordOne.foldByKey(0)(_+_)
    }

    // combineByKey
    def wordcount6(sc : SparkContext): Unit = {
        val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
        val words = rdd.flatMap(_.split(" "))
        val wordOne = words.map((_,1))
        val wordCount: RDD[(String, Int)] = wordOne.combineByKey(
            v=>v,
            (x:Int, y) => x + y,
            (x:Int, y:Int) => x + y
        )
    }

    // countByKey
    def wordcount7(sc : SparkContext): Unit = {
        val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
        val words = rdd.flatMap(_.split(" "))
        val wordOne = words.map((_,1))
        val wordCount: collection.Map[String, Long] = wordOne.countByKey()
    }

    // countByValue
    def wordcount8(sc : SparkContext): Unit = {
        val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
        val words = rdd.flatMap(_.split(" "))
        val wordCount: collection.Map[String, Long] = words.countByValue()
    }

    // reduce, aggregate, fold
    def wordcount91011(sc : SparkContext): Unit = {
        val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
        val words = rdd.flatMap(_.split(" "))

        // 【（word, count）,(word, count)】
        // word => Map[(word,1)]
        val mapWord = words.map(
            word => {
                mutable.Map[String, Long]((word,1))
            }
        )

       val wordCount = mapWord.reduce(
            (map1, map2) => {
                map2.foreach{
                    case (word, count) => {
                        val newCount = map1.getOrElse(word, 0L) + count
                        map1.update(word, newCount)
                    }
                }
                map1
            }
        )

        println(wordCount)
    }

}
