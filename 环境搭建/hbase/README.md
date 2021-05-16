## HBase环境搭建

**安装前置条件：安装好hadoop以及zookeeper，并正常启动**

> 使用1.3.1版本，1.0版本以后，web端口从60010改为16010

### 1.完全分布式部署
- hadoop101为主HMaster， hadoop103为备用的HMaster，所以需要自己新建一个文件backup-masters并写入hadoop103 注意启动的时候在hadoop101
- 完全分布式需要找到HDFS，如果用了HA，那么hbase-site.xml中的hbase.rootdir需要配置成HA的
- 要找到HDFS，有三种方式：
    - 把hadoop的配置目录加到hbase-env.sh的 HBASE_CLASSPATH中 如 export HBASE_CLASSPATH=/usr/local/hadoop/etc/hadoop
    - 把hadoop的hdfs-site.xml拷贝到 $HBASE_HOME/conf下面，也可以采用软链接的方式
    - 把HDFS的配置加到 hbase-site.xml 中
#### 1.0 上传并解压安装包
上传HBase-1.3.1-bin.tar.gz到hadoop101机器的/opt/software目录下

```
# 解压
tar -zxvf /opt/software/HBase-1.3.1-bin.tar.gz -C /opt/app/
# 创建软链接到/usr/local/
xcall sudo ln -s /opt/app/hbase-1.3.1 /usr/local/hbase
```

#### 1.1 修改配置文件
1）hbase-env.sh修改内容
```
export JAVA_HOME=/usr/local/jdk
export HBASE_MANAGES_ZK=false
```
2）hbase-site.xml修改内容
```
<configuration>
    <property>
        <name>hbase.rootdir</name>
        <value>hdfs://hadoop101:8020/hbase</value>
    </property>
    <!-- 以分布式的方式启动hbase，默认是单机模式 -->
    <property>
        <name>hbase.cluster.distributed</name>
        <value>true</value>
    </property>

    <!-- 0.98后的新变动，之前的版本默认端口为60000，现在为16000 -->
    <property>
        <name>hbase.master.port</name>
        <value>16000</value>
    </property>

    <property>
        <name>hbase.zookeeper.quorum</name>
        <value>hadoop101:2181,hadoop102:2181,hadoop103:2181</value>
    </property>

    <property>
        <name>hbase.zookeeper.property.dataDir</name>
        <value>/data/zookeeper/zkData</value>
    </property>
</configuration>
```
3）regionservers
```
hadoop101
hadoop102
hadoop103
```
4）软连接hadoop配置文件到HBase
```
ln -s /usr/local/hadoop/etc/hadoop/core-site.xml /usr/local/hbase/conf/core-site.xml
ln -s /usr/local/hadoop/etc/hadoop/hdfs-site.xml /usr/local/hbase/conf/hdfs-site.xml
```
#### 1.2 分发按照包到hadoop102和hadoop103
```xsync /opt/app/hbase-1.3.1```
#### 1.3 启动步骤
(1)一键启动的方式
```
# 启动所有组件
bin/start-hbase.sh
# 进入shell咯
bin/hbase shell
```
(2)单独启动的方式
```
# 启动master
bin/hbase-daemon.sh start master
# 启动regionserver
bin/hbase-daemon.sh start regionserver
```

#### 端口说明
- 16000：master的RPC端口
- 16010：master的HTTP端口
- 16020：regionServer的RPC端口
- 16030：regionServer的HTTP端口
