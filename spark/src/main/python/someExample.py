# -*- encoding: utf-8 -*-
"""
Created on 12:59 2019/4/16

@author: fancyChuan
@email: 1247375074@qq.com
"""
import json
import urllib3
from pyspark import SparkFiles
from pyspark import SparkConf
from pyspark import SparkContext


def processCallSigns(signs):
    """使用共享连接池查询呼号"""
    # 创建一个连接池
    http = urllib3.PoolManager()
    # 与每条呼号记录相关联的URL
    urls = map(lambda x: "http://73s.com/qsos/%s.json" % x, signs)
    # 创建请求（非阻塞）
    requests = map(lambda x: (x, http.request('GET', x)), urls)
    # 获取结果
    result = map(lambda x: (x[0], json.loads(x[1].data)), requests)
    # 删除空的结果并返回
    return filter(lambda x: x[1] is not None, result)


def usePipe():
    """使用pipe()跟R脚本交互的示例代码"""
    conf = SparkConf().setMaster("local").setAppName("usePipe")
    sc = SparkContext(conf=conf)
    contactsContactList = sc.parallelize()
    # 使用一个R语言外部程序计算每次呼叫的距离
    distScript = "./src/R/finddistance.R"
    distScriptName = "finddistance.R"
    sc.addFile(distScript)

    def hasDistInfo(call):
        """验证一次呼叫是否有计算距离时必需的字段"""
        requiredFields = ["mylat", "mylong", "contactlat", "contactlong"]
        return all(map(lambda f: call[f], requiredFields))

    def formatCall(call):
        """将呼叫按新的格式重新组织以使之可以被R程序解析"""
        return "{0},{1},{2},{3}".format(
            call["mylat"], call["mylong"],
            call["contactlat"], call["contactlong"])

    pipeInputs = contactsContactList.values().flatMap(
        lambda calls: map(formatCall, filter(hasDistInfo, calls)))
    distances = pipeInputs.pipe(SparkFiles.get(distScriptName))
    print distances.collect()