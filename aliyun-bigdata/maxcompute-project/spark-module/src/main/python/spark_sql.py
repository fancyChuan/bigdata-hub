# -*- coding: utf-8 -*-
import sys
from pyspark.sql import SparkSession

try:
    # for python 2
    reload(sys)
    sys.setdefaultencoding('utf8')
except:
    # python 3 not needed
    pass

if __name__ == '__main__':
    spark = SparkSession.builder\
        .appName("spark sql")\
        .config("spark.sql.broadcastTimeout", 20 * 60)\
        .config("spark.sql.crossJoin.enabled", True)\
        .config("odps.exec.dynamic.partition.mode", "nonstrict")\
        .config("spark.sql.catalogImplementation", "odps")\
        .getOrCreate()

    tableName = "mc_test_table"
    ptTableName = "mc_test_pt_table"
    data = [i for i in range(0, 100)]

    # Drop Create
    spark.sql("DROP TABLE IF EXISTS %s" % tableName)
    spark.sql("DROP TABLE IF EXISTS %s" % ptTableName)

    spark.sql("CREATE TABLE %s (name STRING, num BIGINT)" % tableName)
    spark.sql("CREATE TABLE %s (name STRING, num BIGINT) PARTITIONED BY (pt1 STRING, pt2 STRING)" % ptTableName)

    df = spark.sparkContext.parallelize(data, 2).map(lambda s: ("name-%s" % s, s)).toDF("name: string, num: int")
    pt_df = spark.sparkContext.parallelize(data, 2).map(lambda s: ("name-%s" % s, s, "2018", "0601")).toDF("name: string, num: int, pt1: string, pt2: string")

    # 写 普通表
    df.write.insertInto(tableName) # insertInto语义
    df.write.insertInto(tableName, True) # insertOverwrite语义

    # 写 分区表
    # DataFrameWriter 无法指定分区写入 需要通过临时表再用SQL写入特定分区
    df.createOrReplaceTempView("%s_tmp_view" % ptTableName)
    spark.sql("insert into table %s partition (pt1='2018', pt2='0601') select * from %s_tmp_view" % (ptTableName, ptTableName))
    spark.sql("insert overwrite table %s partition (pt1='2018', pt2='0601') select * from %s_tmp_view" % (ptTableName, ptTableName))

    pt_df.write.insertInto(ptTableName) # 动态分区 insertInto语义
    pt_df.write.insertInto(ptTableName, True) # 动态分区 insertOverwrite语义

    # 读 普通表
    rdf = spark.sql("select name, num from %s" % tableName)
    print("rdf count, %s\n" % rdf.count())
    rdf.printSchema()

    # 读 分区表
    rptdf = spark.sql("select name, num, pt1, pt2 from %s where pt1 = '2018' and pt2 = '0601'" % ptTableName)
    print("rptdf count, %s" % (rptdf.count()))
    rptdf.printSchema()


