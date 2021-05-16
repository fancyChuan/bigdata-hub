## 配置hbase与hive集成

#### 0. 重新编译依赖包
注意：hbase1.3.1与hive1.2.1版本不兼容，因此需要对**hive-hbase-handler-1.2.1.jar**重新编译，
编译过程参考：[Hive-1.2.1集成HBase-1.3.1不兼容问题](https://blog.csdn.net/qq_31024823/article/details/86701768)

将编译后的hive-hbase-handler-1.2.1.jar 替换掉 /usr/local/hive/lib/hive-hbase-handler-1.2.1.jar

#### 1. 将hbase相关jar以软链接的形式copy到$HIVE_HOME/lib下
```
export HBASE_HOME=/usr/local/hbase
export HIVE_HOME=/usr/local/hive

ln -s $HBASE_HOME/lib/hbase-common-1.3.1.jar  $HIVE_HOME/lib/hbase-common-1.3.1.jar
ln -s $HBASE_HOME/lib/hbase-server-1.3.1.jar $HIVE_HOME/lib/hbase-server-1.3.1.jar
ln -s $HBASE_HOME/lib/hbase-client-1.3.1.jar $HIVE_HOME/lib/hbase-client-1.3.1.jar
ln -s $HBASE_HOME/lib/hbase-protocol-1.3.1.jar $HIVE_HOME/lib/hbase-protocol-1.3.1.jar
ln -s $HBASE_HOME/lib/hbase-it-1.3.1.jar $HIVE_HOME/lib/hbase-it-1.3.1.jar
ln -s $HBASE_HOME/lib/htrace-core-3.1.0-incubating.jar $HIVE_HOME/lib/htrace-core-3.1.0-incubating.jar
ln -s $HBASE_HOME/lib/hbase-hadoop2-compat-1.3.1.jar $HIVE_HOME/lib/hbase-hadoop2-compat-1.3.1.jar
ln -s $HBASE_HOME/lib/hbase-hadoop-compat-1.3.1.jar $HIVE_HOME/lib/hbase-hadoop-compat-1.3.1.jar

```
> 删除软链接的命令
```
rm /usr/local/hive/lib/hbase-common-1.3.1.jar  ;
rm /usr/local/hive/lib/hbase-server-1.3.1.jar  ;
rm /usr/local/hive/lib/hbase-client-1.3.1.jar  ;
rm /usr/local/hive/lib/hbase-protocol-1.3.1.jar  ;
rm /usr/local/hive/lib/hbase-it-1.3.1.jar  ;
rm /usr/local/hive/lib/htrace-core-3.1.0-incubating.jar  ;
rm /usr/local/hive/lib/hbase-hadoop2-compat-1.3.1.jar  ;
rm /usr/local/hive/lib/hbase-hadoop-compat-1.3.1.jar  ;
```
#### 修改hive-site.xml的配置
主要修改zookeeper的配置项，以便能够访问到hbase的master
```
    <!--配置hbase与hive集成-->
    <property>
        <name>hive.zookeeper.quorum</name>
        <value>hadoop101,hadoop102,hadoop103</value>
        <description>The list of ZooKeeper servers to talk to. This is only needed for read/write locks.</description>
    </property>
    <property>
        <name>hive.zookeeper.client.port</name>
        <value>2181</value>
        <description>The port of ZooKeeper servers to talk to. This is only needed for read/write locks.</description>
    </property>
```
> 完整配置参见：[hive-site.xml](与hive集成/hive-site.xml)

最后，重启hive即可