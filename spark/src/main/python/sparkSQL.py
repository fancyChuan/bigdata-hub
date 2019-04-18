# -*- encoding: utf-8 -*-
"""
Created on 18:03 2019/4/18

@author: fancyChuan
@email: 1247375074@qq.com
"""
from pyspark.sql import SparkSession

spark = SparkSession.builder.appName("hello-sparkSQL").getOrCreate()
df = spark.read.json("E:\\JavaWorkshop\\bigdata-learn\spark\src\main\\resources\\jsonFile.txt")

df.show()

df.printSchema()