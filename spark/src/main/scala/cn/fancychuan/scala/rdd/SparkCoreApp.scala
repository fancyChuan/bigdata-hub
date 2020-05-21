package cn.fancychuan.scala.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * spark core
  * RDD编程
  */
object SparkCoreApp {

    def main(args: Array[String]): Unit = {

        val conf = new SparkConf().setMaster("local[*]").setAppName("SparkCoreRDD")
        val sc = new SparkContext(conf)

        // makeRDD的底层实现就是parallelize，传入的第一个参数要求是Seq需要有顺序，那么Array和List都可以
        val rdd0: RDD[Int] = sc.makeRDD(List(1,2,3,4,5,6)) // 在local模式中，默认分区数是使用的内核数与2相对的较大值。比如local[4]就是4与2相比，取较大值4，由此默认分区就是4
        val rdd1: RDD[Int] = sc.makeRDD(Array(1,2,3,4,5,6,7,8,9,10,12,13,14), 3) // 有三个分区
        val rdd2: RDD[Int] = sc.makeRDD(List(1,2,3,4,5,6), 1) // 指定了分区数

//        rdd0.saveAsTextFile("spark/target/rdd0")
//        rdd1.saveAsTextFile("spark/target/rdd1")
//        rdd2.saveAsTextFile("spark/target/rdd2")

        // 读取文件时，传递的第二个参数是最小分区数，但实际上的分区数不一定是这个，跟hadoop的切片规则有关
        val rdd3: RDD[String] = sc.textFile("spark/data/testTextFile.txt", 2)

        // rdd1.saveAsTextFile("spark/target/test1")

        // map算子
        val mapRDD: RDD[Int] = rdd1.map(x => x * 2) // 也可以写成 rdd1.map(_*2) 因为只用到一个变量x
        // mapPartition算子，减少了发送到执行器的交互次数。但是数据较多的时候容易内容溢出
        val mapPartRDD: RDD[Int] = rdd1.mapPartitions( items => items.map(_*3))
        // mapPartitionsWithIndex算子
        val mapPartIndexRDD: RDD[String] = rdd1.mapPartitionsWithIndex { // TODO：为什么这里用大括号，而且下一行用case？模式匹配？
            case (index, items) => {
                items.map(_ + "分区号：" + index)
            }
        }
        mapPartIndexRDD.collect().foreach(println)

        // glom算子
        val glomRDD: RDD[Array[Int]] = rdd1.glom()
        glomRDD.collect().foreach(items => println(items.mkString(",")))
        // groupBy算子
        val groupByRDD1: RDD[(Int, Iterable[Int])] = rdd1.groupBy(_ % 4) // 拥有3个分区的rdd使用groupBy
        val groupByRDD2: RDD[(Int, Iterable[Int])] = rdd2.groupBy(_ % 2) // 只有1个分区的rdd使用groupBy
        groupByRDD1.collect().foreach(println)
        groupByRDD2.collect().foreach(println)
        //groupByRDD1.saveAsTextFile("spark/target/groupby1") // rdd1是3个分区，groupByRDD1也是三个分区
        //groupByRDD2.saveAsTextFile("spark/target/groupby2") // TODO：那groupby的网络分发细节是怎么样的？

        // sample(withReplacement, fraction, seed)
        val sampleRDD: RDD[Int] = rdd1.sample(true, 0.4)
        // distinct([numPartitions]))  将去重的数据重新放到numPartitions个分区中（顺序打乱重组，也就是发生了shuffle）
        val distinctRDD: RDD[Int] = rdd1.distinct(2) // rdd1是3个分区，执行之后，distinctRDD是2个分区
        // distinctRDD.saveAsTextFile("spark/target/distinct")

        // coalesce(numPartitions)缩减分区数，用于大数据集过滤后，提高小数据集的执行效率
        // coalesce重新分区，可以选择是否进行shuffle过程
        val coalRDD: RDD[Int] = rdd1.coalesce(2) // 指定的分区数如果大于父RDD的话是不生效的
        println(coalRDD.partitions.size)
        // repartition实际上是调用的coalesce，默认是进行shuffle的
        rdd1.repartition(2) // 重新分区
    }

}
