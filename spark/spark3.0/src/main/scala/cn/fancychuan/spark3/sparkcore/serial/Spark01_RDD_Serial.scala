package cn.fancychuan.spark3.sparkcore.serial

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * scala中样例类默认实现序列化，也就是说有序列化的特质
 * spark中
 * - 算子之外的代码在Driver执行
 * - 算子之内的代码在Executor执行
 * 会导致算子内经常会用到算子外的数据，这样就形成了闭包的效果，
 * 因此需要能够将算子外的数据传输到算子内，就需要序列化
 *
 * TODO：实际工作什么情况才会用到？
 */
object Spark01_RDD_Serial {

    def main(args: Array[String]): Unit = {
        val sparConf = new SparkConf().setMaster("local").setAppName("WordCount")
        val sc = new SparkContext(sparConf)

        val rdd: RDD[String] = sc.makeRDD(Array("hello world", "hello spark", "hive", "atguigu"))

        val search = new Search("s") // 这是一个定义在算子外的对象

        //search.getMatch1(rdd).collect().foreach(println)
        search.getMatch2(rdd).collect().foreach(println)

        sc.stop()
    }
    // 查询对象
    // 类的构造参数其实是类的属性, 构造参数需要进行闭包检测，其实就等同于类进行闭包检测
    class Search(query:String){


        def isMatch(s: String): Boolean = {
            s.contains(query)
            // 上面的代码等价于：s.contains(this.query)，说明这里的query其实不是普通字符串，而是类的属性
        }

        // 函数序列化案例
        def getMatch1 (rdd: RDD[String]): RDD[String] = {
            rdd.filter(isMatch) // isMatch函数用到了算子外部的类的属性query，那么类需要能序列化
        }

        // 属性序列化案例
        def getMatch2(rdd: RDD[String]): RDD[String] = {
            val s = query // 这种方式Search类就可以不用序列化
            rdd.filter(x => x.contains(s)) // 这里将query的作用域转化掉了，变成了字符串s，而s本身可以序列化
        }
    }
}
