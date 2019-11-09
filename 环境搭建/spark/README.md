## Spark环境搭建

### 1. Local模式【本地部署】
解压后直接使用
```
tar -zxvf spark-2.3.3-bin-hadoop2.7.tgz
```
求PI的案例
```
# cd spark-2.3.3-bin-hadoop2.7

bin/spark-submit \
--class org.apache.spark.examples.SparkPi \
--executor-memory 1G \
--total-executor-cores 2 \
./examples/jars/spark-examples_2.11-2.3.3.jar \
100
```

### 2. standalone模式【独立部署】
构建一个由Master+Slave构成的Spark集群，Spark运行在集群中。
```
# cd spark-2.3.3-bin-hadoop2.7/conf
# 修改slaves文件
mv slaves.template slaves
vim slaves
---------------
s01
s02
s03
---------------

# 修改spark-env.sh 把s01配置为master
vim spark-env.sh
---------------
SPARK_MASTER_HOST=s01
SPARK_MASTER_PORT=7077
---------------

# 分发spark包到其他机器上
xsync /opt/app/spark-2.3.3-bin-hadoop2.7

# 配置java环境
vim sbin/spark-config.sh
--------------
export JAVA_HOME=/usr/local/jdk
--------------

# 启动
sbin/start-all.sh
```

测试：
```
bin/spark-submit \
--class org.apache.spark.examples.SparkPi \
--master spark://s01:7077 \
--executor-memory 1G \
--total-executor-cores 2 \
./examples/jars/spark-examples_2.11-2.3.3.jar \
100

```
启动spark shell
```
bin/spark-shell \
--master spark://hadoop102:7077 \
--executor-memory 1g \
--total-executor-cores 2
```
#### JobHistoryServer配置
- 修改名称 ```mv spark-defaults.conf.template spark-defaults.conf```
- 修改spark-default.conf文件，开启log
```
spark.eventLog.enabled           true
spark.eventLog.dir               hdfs://s01:9000/sparklogs
```
> 在HDFS上需要存在 /sparklogs 这个目录。另外要使用NameNode的rpc端口，注意修改成实际的端口 
> TODO：如果是高可用的NameNode，该如何适配？？
- 修改spark-env.sh，加上如下配置：
```
export SPARK_HISTORY_OPTS="-Dspark.history.ui.port=18080 
-Dspark.history.retainedApplications=30 
-Dspark.history.fs.logDirectory=hdfs://s01:9000/sparklogs"
```
> 参数说明
> - spark.eventLog.dir：Application在运行过程中所有的信息均记录在该属性指定的路径下； 
> - spark.history.ui.port=18080  WEBUI访问的端口号为18080
> - spark.history.fs.logDirectory=hdfs://hadoop102:9000/directory  配置了该属性后，在start-history-server.sh时就无需再显式的指定路径，Spark History Server页面只展示该指定路径下的信息
> - spark.history.retainedApplications=30指定保存Application历史记录的个数，如果超过这个值，旧的应用程序信息将被删除，这个是内存中的应用数，而不是页面上显示的应用数。
- 重新分发文件
```
xsync spark-defaults.conf
xsync spark-env.sh
```
- 启动历史服务
```sbin/start-history-server.sh```

### 3. 


### 4. python等跟spark结合的环境搭建

[spark学习环境搭建.md](../../spark/spark学习环境搭建.md)