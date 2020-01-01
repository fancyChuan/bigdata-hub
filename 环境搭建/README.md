## Hadoop生态环境搭建

集群规划【完全分布式，非HA】

组件 | hadoop101 | hadoop102 | hadoop103
--- | --- | --- | ---
HDFS | NameNode/DataNode | 2nd NameNode/DataNode | DataNode
YARN | ResourceManager/NodeManager | NodeManager | NodeManager
hadoop其他| jobhistoryserver
mysql | mysqld
hive | | hiveserver2 | metastore

搭建经验：
- 目录规划管理
    - 所有可执行代码包全部放在 /opt/software
    - java/scala等通用的放在 /opt/modules
    - 大数据组件同意放在      /opt/app
- 所有的框架都需要做一个软链接到/usr/local/下。比如 ln -s /opt/app/hive-1.2.1 /usr/local/hive
- /etc/profile的内容可以如下：
```
# java scala
export JAVA_HOME=/usr/local/jdk
export SCALA_HOME=/usr/local/scala
export PATH=$PATH:$JAVA_HOME/bin:$SCALA_HOME/bin

# build
export GRADLE_HOME=/usr/local/gradle
export M2_HOME=/usr/local/maven
export PATH=$PATH:$M2_HOME/bin:$GRADLE_HOME/bin

# bigdata
export ZOOKEEPER_HOME=/usr/local/zookeeper
export HADOOP_HOME=/usr/local/hadoop
export HIVE_HOME=/usr/local/hive
export HBASE_HOME=/usr/local/hbase
export FLUME_HOME=/usr/local/flume
export KAFKA_HOME=/usr/local/kafka
export SPARK_HOME=/usr/local/spark

export PATH=$PATH:$ZOOKEEPER_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
export PATH=$PATH:$FLUME_HOME/bin:$KAFKA_HOME/bin
export PATH=$PATH:$HIVE_HOME/bin:$HBASE_HOME/bin:$SPARK_HOME/bin
```



