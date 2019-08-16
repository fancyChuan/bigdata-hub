## Hadoop生态常用基本配置
说明：
- 配置涉及的s00/s01/s02/s03需要根据实际情况调整，
- 配置涉及的目录也需要调整
- 所有的框架都需要做一个软链接到/usr/local/下。比如 ln -s /opt/app/hive-1.2.1 /usr/local/hive

### 1. Hadoop
#### 1.1 伪分布式
#### 1.2 完全分布式
#### 1.3 HA
基本配置：[hadoop/HA部署](https://github.com/fancyChuan/bigdata-learn/tree/master/环境搭建/hadoop/HA部署)

使用说明
- 修改hadoop-env.xml中JAVA_HOME，哪怕在/etc/profile配置了最好也还是设置下，不然启动hdfs等会有问题
- 将s01/s02/s03替换成真实的主机名，比如hadoop01

启动步骤
- 在集群上每个节点启动journalnode
```
# 方式1：手动到每个节点执行命令
sbin/hadoop-daemon.sh start journalnode
# 方式2：在master上启动所有journalnode
sbin/hadoop-daemons.sh start journalnode
```
- 格式化HDFS
```
# 在s01上执行
bin/hdfs namenode -format
# 把s01的hadoop.tmp.dir所配置的目录分发到s02下，因为s01和s02都需要运行NameNode，需要是同个HDFS，clusterID需要唯一
# 方式1
scp -r /data/hadoop/tmp hadoop@s02://data/hadoop/
# 方式2(推荐)：在s02上执行
bin/hdfs namenode -bootstrapStandby
```
- 格式化ZK，配置和启动参考zookeeper部分
```
bin/hdfs zkfc -formatZK
```
- 启动HDFS、YARN
```
sbin/start-dfs.sh
sbin/start-yarn.sh
```
- 启动历史日志服务器
```
sbin/mr-jobhistory-daemon.sh start jobhistoryserver
```

### 2. ZooKeeper
基本配置：[zookeeper](https://github.com/fancyChuan/bigdata-learn/tree/master/环境搭建/zookeeper)

使用说明：注意配置的目录地址以及需要更换主机名，把配置文件放在 $ZOOKEEPER_HOME/conf下面

启动步骤
```
# 1. 创建myid，每台主机需要有个唯一的id号，且与zoo.cfg中的server.X的X一致
进入dataDir=/data/zookeeper/data，然后vim myid 输入数字，然后保存
# 2. 在每个节点启动
bin/zkServer.sh start
```

### 3. Hive
使用1.2.1版本，2.0版本以后可能会不支持MR引擎，更多的是使用spark

基本配置：[hive](https://github.com/fancyChuan/bigdata-learn/tree/master/环境搭建/hive)

启动步骤
```
# 启动metastore服务在后台运行
nohup hive --service metastore &
# 启动hiveserver2
nohup hive --service hiveserver2 &
# 启动hive cli或者beeline
```

### 4. Hbase
使用1.1.2版本，1.0版本以后，web端口从60010改为16010

基本配置：[hbase](https://github.com/fancyChuan/bigdata-learn/tree/master/环境搭建/hbase)

启动步骤
```
# 启动所有组件
bin/start-hbase.sh
# 进入shell咯
bin/hbase shell
```

完全分布式部署
- s01为主HMaster， s03为备用的HMaster，所以需要自己新建一个文件backup-masters并写入s03 注意启动的时候在s01
- 完全分布式需要找到HDFS，如果用了HA，那么hbase-site.xml中的hbase.rootdir需要配置成HA的
- 要找到HDFS，有三种方式：
    - 把hadoop的配置目录加到hbase-env.sh的 HBASE_CLASSPATH中 如 export HBASE_CLASSPATH=/usr/local/hadoop/etc/hadoop
    - 把hadoop的hdfs-site.xml拷贝到 $HBASE_HOME/conf下面
    - 把HDFS的配置加到 hbase-site.xml 中
    
> 看到1.1.2版本lib下的hadoop相关jar包是2.5.1的，而集群用的是2.7.1，是否会有问题？？