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
```

#### 官方示例：
- 统计hbase中的student表有多少行
```
cd /usr/local/hbase
yarn jar lib/hbase-server-1.3.1.jar rowcounter student 
```

- 使用MR将本地数据导入HBase
```
# 创建本地文件vim fruit.tsv
1001    Apple   Red+
1002    Pear    Yellow
1003    Pineapple       Yellow
1004    Done    Gogogo

# 将文件上传到HDFS
hdfs dfs -put fruit.tsv /forlearn/

# 执行MR
cd /usr/local/hbase
yarn jar lib/hbase-server-1.3.1.jar importtsv -Dimporttsv.columns=HBASE_ROW_KEY,info:name,info:color fruit hdfs://hadoop101:8020/forlearn/fruit.tsv
``` 


#### 自定义HBase-MapReduce
