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

官方示例：
- 统计hbase中的student表有多少行
