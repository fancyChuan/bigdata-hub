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
#### 2.1 基础部署
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
#### 2.2JobHistoryServer配置
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

#### 2.3 HA配置
- vim spark-env.sh
```
注释掉如下内容：
#SPARK_MASTER_HOST=hadoop102
#SPARK_MASTER_PORT=7077
添加上如下内容：
export SPARK_DAEMON_JAVA_OPTS="
-Dspark.deploy.recoveryMode=ZOOKEEPER 
-Dspark.deploy.zookeeper.url=hadoop102,hadoop103,hadoop104 
-Dspark.deploy.zookeeper.dir=/spark"
```
- 分发方法
- 启动
```
# 在hadoop102上启动所有节点（包括）
sbin/start-all.sh
# 在hadoop103上再单独启动一个master节点
sbin/start-master.sh
```
使用
```
bin/spark-shell \
--master spark://hadoop102:7077,hadoop103:7077 \
--executor-memory 2g \
--total-executor-cores 2
```

### 3. Yarn模式部署
Spark客户端直接连接Yarn，不需要额外构建Spark集群。有yarn-client和yarn-cluster两种模式
- yarn-client：Driver程序运行在客户端，适用于交互、调试，希望立即看到app的输出
- yarn-cluster：Driver程序运行在由RM（ResourceManager）启动的AP（APPMaster）适用于生产环境。

安装配置：
- 修改yarn-site.xml，添加下面的配置
```
        <!--是否启动一个线程检查每个任务正使用的物理内存量，如果任务超出分配值，则直接将其杀掉，默认是true -->
        <property>
                <name>yarn.nodemanager.pmem-check-enabled</name>
                <value>false</value>
        </property>
        <!--是否启动一个线程检查每个任务正使用的虚拟内存量，如果任务超出分配值，则直接将其杀掉，默认是true -->
        <property>
                <name>yarn.nodemanager.vmem-check-enabled</name>
                <value>false</value>
        </property>
```
- 修改spark-env.sh
```
YARN_CONF_DIR=/usr/local/hadoop/etc/hadoop
```
- 分发文件(不需要再启动spark的master和worker了，因为任务提交给了yarn)

日志查看
- 修改spark-defaults.conf，添加如下内容
```
spark.yarn.historyServer.address=hadoop102:18080
spark.history.ui.port=18080
```
- 重启spark历史服务
```
sbin/stop-history-server.sh 
sbin/start-history-server.sh
```

### 4. python等跟spark结合的环境搭建

[spark学习环境搭建.md](../../spark/spark学习环境搭建.md)