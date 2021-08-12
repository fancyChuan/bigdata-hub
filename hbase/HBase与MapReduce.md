## HBase与MapReduce

通过yarn来跑MR，在MR里面操作hbase，需要用到hbase的相关jar包，有2种方式：
- 方式1：在命令行终端临时设置HADOOP_CLASSPATH环境变量
```
export HBASE_HOME=/usr/local/hbase
export HADOOP_HOME=/usr/local/hadoop
export HADOOP_CLASSPATH=`${HBASE_HOME}/bin/hbase mapredcp`
```
- 方式2：永久生效，在/etc/profile修改
```
export HBASE_HOME=/usr/local/hbase
export HADOOP_HOME=/usr/local/hadoop
```
同时修改 /usr/local/hadoop/etc/hadoop/hadoop-env.sh
```
# 在最后面加上
export HADOOP_CLASSPATH=$HADOOP_CLASSPATH:/usr/local/hbase/lib/*

# 也可以写成
export HBASE_LIBS=$(hbase mapredcp)
export HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$HBASE_LIBS
```

#### 官方示例：
官方通过 hbase-server-1.3.1.jar 这个jar报提供了一系列MR方法
- 统计hbase中的student表有多少行
```
cd /usr/local/hbase
yarn jar lib/hbase-server-1.3.1.jar rowcounter student 
```
- 统计hbase中的student表有多少cells
```
# CellCounter方法，第一个参数是表名，第二个参数是结果输出的HDFS目录，第三个参数是列
hadoop jar lib/hbase-server-1.3.1.jar CellCounter student /forlearn/hbase/cellcount ,
# 查看结果
hdfs dfs -text /forlearn/hbase/cellcount/*
```

- 使用MR将本地数据导入HBase
```
# 先在hbase中创建表
hbase(main):004:0> create 'fruit','info'

# 创建本地文件vim fruit.tsv
1001    Apple   Red+
1002    Pear    Yellow
1003    Pineapple       Yellow
1004    Done    Gogogo

# 将文件上传到HDFS
hdfs dfs -put /home/appuser/forlearn/hbase/fruit.tsv /forlearn/hbase/

# 执行MR
cd /usr/local/hbase
yarn jar lib/hbase-server-1.3.1.jar importtsv -Dimporttsv.columns=HBASE_ROW_KEY,info:name,info:color fruit hdfs://hadoop101:8020/forlearn/hbase/fruit.tsv
# 上面的命令中 HBASE_ROW_KEY 代表rowkey列，要注意数据文件中的字段顺序要与importtsv.columns的顺序一致
``` 

> 如果想在HBase-MR中添加参数，一种方式是用-D在命令行加参数（可能不能使用），另一种方式是在core-site.xml中添加

```
# hbase-server-1.3.1.jar包含的方法
[appuser@hadoop102 hbase]$ hadoop jar lib/hbase-server-1.3.1.jar                 
An example program must be given as the first argument.
Valid program names are:
  CellCounter: Count cells in HBase table.
  WALPlayer: Replay WAL files.
  completebulkload: Complete a bulk data load.
  copytable: Export a table from local cluster to peer cluster.
  export: Write table data to HDFS.
  exportsnapshot: Export the specific snapshot to a given FileSystem.
  import: Import data written by Export.
  importtsv: Import data in TSV format.
  rowcounter: Count rows in HBase table.
  verifyrep: Compare the data from tables in two different clusters. WARNING: It doesn't work for incrementColumnValues'd cells since the timestamp is changed after being appended to the log.
```


#### 自定义HBase-MapReduce
目标：将fruit表中的一部分数据，通过MR迁入fruit_mr表中

