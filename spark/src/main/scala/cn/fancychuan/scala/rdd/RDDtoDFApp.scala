package cn.fancychuan.scala.rdd

import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}

object RDDtoDFApp {

  def rddToDF(sparkSession: SparkSession): DataFrame = {
    val schema = StructType(Seq(
      StructField("name", StringType, true)
      , StructField("age", IntegerType, true)
    ))

  }
}
