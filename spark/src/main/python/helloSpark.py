# -*- encoding: utf-8 -*-
"""
Created on 11:36 PM 4/8/2019

@author: fancyChuan
@email: 1247375074@qq.com
"""

from pyspark import SparkConf, SparkContext

conf = SparkConf().setMaster("local").setAppName("helloSpark")
sc = SparkContext(conf=conf)

lines = sc.textFile("../resources/testfile.md")
pythonLines = lines.filter(lambda line: "Python" in line)

print(pythonLines.first())

sc.stop()