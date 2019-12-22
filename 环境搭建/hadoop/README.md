## hadoop搭建

注意
- 修改hadoop-env.xml中JAVA_HOME，哪怕在/etc/profile配置了最好也还是设置下，不然启动hdfs等会有问题
- 在hadoop101上修改完配置记得xsync分发到另外的机器上
- 要注意ResourceManager配置在那台机器上，就需要到那台机器上执行 sbin/yarn-daemon.sh start resourcemanager

#### 0 解压缩
```
tar -zxvf /opt/software/hadoop-2.7.2.tar.gz -C /opt/app/ 
sudo ln -s /opt/app/hadoop-2.7.2  /usr/local/hadoop
```

#### 1 伪分布式
#### 2 完全分布式
基本配置：[hadoop完全分布式部署](https://github.com/fancyChuan/bigdata-learn/tree/master/环境搭建/hadoop/完全分布式部署)

启动
- 首次使用得时候需要格式化HDFS。在hadoop101上执行 ```hdfs namenode -format```
- 启动HDFS、YARN
```
# 在hadoop101执行
sbin/start-dfs.sh
sbin/start-yarn.sh
```
- 启动历史日志服务器
```
# 在hadoop101执行
sbin/mr-jobhistory-daemon.sh start jobhistoryserver
```

#### 3 HA部署
基本配置：[hadoop/HA部署](https://github.com/fancyChuan/bigdata-learn/tree/master/环境搭建/hadoop/HA部署)

配置好了之后，需要执行下面得操作：
- 在集群上每个节点启动journalnode
```
# 方式1：手动到每个节点执行命令
sbin/hadoop-daemon.sh start journalnode
# 方式2：在master上启动所有journalnode
sbin/hadoop-daemons.sh start journalnode
```
- 格式化HDFS【已经格式化过得就不需要】
```
# 在hadoop101上执行
bin/hdfs namenode -format
```
- 把hadoop101的hadoop.tmp.dir所配置的目录分发到hadoop102下，
```
# 因为hadoop101和hadoop102都需要运行NameNode，需要是同个HDFS，clusterID需要唯一
# 方式1
scp -r /data/hadoop/tmp hadoop@hadoop102://data/hadoop/
# 方式2(推荐)：在hadoop102上执行
hdfs namenode -bootstrapStandby
```
- 格式化ZK，配置和启动参考zookeeper部分：[zk安装启动](../zookeeper)
```
xcall cd /usr/local/zookeeper/;zkServer.sh start
hdfs zkfc -formatZK
```

> TODO: 待补充

#### 4 YARN高可用
- 修改yarn-site.xml并分发
```
<configuration>
    
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    
    <!--启用resourcemanager ha-->
    <property>
        <name>yarn.resourcemanager.ha.enabled</name>
        <value>true</value>
    </property>
    
    <!--声明两台resourcemanager的地址-->
    <property>
        <name>yarn.resourcemanager.cluster-id</name>
        <value>cluster-yarn1</value>
    </property>
    
    <property>
        <name>yarn.resourcemanager.ha.rm-ids</name>
        <value>rm1,rm2</value>
    </property>
    
    <property>
        <name>yarn.resourcemanager.hostname.rm1</name>
        <value>hadoop102</value>
    </property>
    
    <property>
        <name>yarn.resourcemanager.hostname.rm2</name>
        <value>hadoop103</value>
    </property>
    
    <!--指定zookeeper集群的地址-->
    <property>
        <name>yarn.resourcemanager.zk-address</name>
        <value>hadoop102:2181,hadoop103:2181,hadoop104:2181</value>
    </property>
    
    <!--启用自动恢复-->
    <property>
        <name>yarn.resourcemanager.recovery.enabled</name>
        <value>true</value>
    </property>
    
    <!--指定resourcemanager的状态信息存储在zookeeper集群-->
    <property>
        <name>yarn.resourcemanager.store.class</name>
        <value>org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore</value>
    </property>
</configuration>
```
	
- 启动hdfs
```
（1）在各个JournalNode节点上，输入以下命令启动journalnode服务：
sbin/hadoop-daemon.sh start journalnode
（2）在[nn1]上，对其进行格式化，并启动：
bin/hdfs namenode -format
sbin/hadoop-daemon.sh start namenode
（3）在[nn2]上，同步nn1的元数据信息：
bin/hdfs namenode -bootstrapStandby
（4）启动[nn2]：
sbin/hadoop-daemon.sh start namenode
（5）启动所有DataNode
sbin/hadoop-daemons.sh start datanode
（6）将[nn1]切换为Active
bin/hdfs haadmin -transitionToActive nn1
5.	启动YARN
（1）在hadoop102中执行：
sbin/start-yarn.sh
（2）在hadoop103中执行：
sbin/yarn-daemon.sh start resourcemanager
（3）查看服务状态，如图3-24所示
bin/yarn rmadmin -getServiceState rm1
```
