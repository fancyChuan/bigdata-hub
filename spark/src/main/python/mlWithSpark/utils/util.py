# -*- encoding: utf-8 -*-
"""
Created on 11:38 2019/4/30

@author: fancyChuan
@email: 1247375074@qq.com
"""
from pyspark import SparkConf, SparkContext
from pyspark.sql import SparkSession
from pyspark.sql.types import *

conf = SparkConf().setAppName("First Spark App").setMaster("local[*]")
sc = SparkContext(conf=conf)
spark = SparkSession(sc)


def get_user_data():
    custom_schema = StructType([
        StructField("no", StringType(), True),
        StructField("age", IntegerType(), True),
        StructField("gender", StringType(), True),
        StructField("occupation", StringType(), True),
        StructField("zipCode", StringType(), True)])

    from pyspark.sql import SQLContext

    sqlContext = SQLContext(sc)
    user_df = sqlContext.read.format("com.databricks.spark.csv").options(header=None, delimiter="|").load(
        "E:\\JavaWorkshop\\bigdata-learn\\spark\\data\\ml-100k\\u.user", schema=custom_schema)
    return user_df
