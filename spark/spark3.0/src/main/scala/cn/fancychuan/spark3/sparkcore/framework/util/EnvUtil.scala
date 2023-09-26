package cn.fancychuan.spark3.sparkcore.framework.util

import org.apache.spark.SparkContext

/**
 * ThreadLocal可以对线程的内存进行控制，存储数据，共享数据
 * ThreadLocal不解决线程安全问题
 */
object EnvUtil {

    private val scLocal = new ThreadLocal[SparkContext]()

    def put( sc : SparkContext ): Unit = {
        scLocal.set(sc)
    }

    def take(): SparkContext = {
        scLocal.get()
    }

    def clear(): Unit = {
        scLocal.remove()
    }
}
