# -*- encoding: utf-8 -*-
"""
Created on 16:07 2019/4/9

@author: fancyChuan
@email: 1247375074@qq.com
@desc: 向spark传递函数 示例
"""
from pyspark import SparkConf, SparkContext


class SearchFunctionsWarn(object):
    """
    不推荐的写法，rdd.filter()传递函数的时候会把整个self都传递到集群上，可能self非常大
    """
    def __init__(self, query):
        self.query = query

    def isMatch(self, s):
        return self.query in s

    def getMatchesFunctionReference(self, rdd):
        return rdd.filter(self.isMathch)  # 有风险

    def getMatchesMemberReference(self, rdd):
        return rdd.filter(self.isMatch)  # 有风险


class SearchFunctionsRight(object):
    """
    推荐的写法，使用局部变量，把需要传递的内容赋值给局部变量，然后传递局部变量
    结论： 传递局部可序列化变量或者顶层函数始终是安全
    """
    def __init__(self, query):
        self.query = query

    def isMatch(self, s):
        return self.query in s

    def getMatchesFunctionReference(self, rdd):
        query = self.query
        return rdd.filter(lambda x: query in x)

    def getMatchesMemberReference(self, rdd):
        query = self.query
        return rdd.filter(lambda x: query in x)


if __name__ == "__main__":

    searchPython = SearchFunctionsWarn("Python")

    conf = SparkConf().setMaster("local").setAppName("passFunction")
    sc = SparkContext(conf=conf)
    linesRDD = sc.textFile("../resources/testfile.md")

    result = searchPython.getMatchesMemberReference(linesRDD)
    print(result.count())
    print(result.collect())