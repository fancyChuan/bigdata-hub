package cn.fancychuan.scala.accumulator

import org.apache.spark.util.AccumulatorV2

import scala.collection.mutable

/**
  * 使用V2版本的累加器
  */
class SessionAggrStatAccumulator extends AccumulatorV2[String, mutable.HashMap[String, Int]]{

  private val aggrStatMap = mutable.HashMap[String, Int]() // 保存所有聚合数据

  // 判断是否为初始值
  override def isZero: Boolean = {
    aggrStatMap.isEmpty
  }

  override def copy(): AccumulatorV2[String, mutable.HashMap[String, Int]] = {
    val newAcc = new SessionAggrStatAccumulator
    aggrStatMap.synchronized {
      newAcc.aggrStatMap ++= this.aggrStatMap
    }
    newAcc
  }

  override def reset(): Unit = ???

  override def add(v: String): Unit = ???

  override def merge(other: AccumulatorV2[String, mutable.HashMap[String, Int]]): Unit = ???

  override def value: mutable.HashMap[String, Int] = ???
}
