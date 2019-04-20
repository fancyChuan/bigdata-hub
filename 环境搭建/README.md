## Hadoop生态常用基本配置

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
- 格式化ZK
```
bin/hdfs zkfc -formatZK
```
- 启动HDFS、YARN
```
sbin/start-dfs.sh
sbin/start-yarn.sh
```