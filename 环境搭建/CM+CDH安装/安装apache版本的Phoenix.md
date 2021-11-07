## 安装Phoenix

### 安装过程
```
# 1.解压，并将安装包重命名
tar -zxvf apache-phoenix-4.14.2-HBase-1.3-bin.tar.gz -C /opt/apps/
mv apache-phoenix-4.14.2-HBase-1.3-bin phoenix-4.14.2-HBase-1.3-bin
# 2.将安装包分发到hadoop102和103
xsync /opt/apps/phoenix-4.14.2-HBase-1.3-bin
# 3.创建软链接到/usr/local/phoenix
xcall sudo ln -s /opt/apps/phoenix-4.14.2-HBase-1.3-bin /usr/local/phoenix
# 4.修改环境变量
# sudo vim /etc/profile
export PHOENIX_HOME=/usr/local/phoenix
export PHOENIX_CLASSPATH=$PHOENIX_HOME
export PATH=$PATH:$PHOENIX_HOME/bin
# 5. 分发配置文件
sudo xsync /etc/profile
# 6. 将Phoenix的两个jar包创建到hbase/lib的软链接
xcall ln -s /usr/local/phoenix/phoenix-4.14.2-HBase-1.3-server.jar /usr/local/hbase/lib/phoenix-4.14.2-HBase-1.3-server.jar
xcall ln -s /usr/local/phoenix/phoenix-4.14.2-HBase-1.3-client.jar /usr/local/hbase/lib/phoenix-4.14.2-HBase-1.3-client.jar
# 7. 启动hadoop,zk,hbase后，启动Phoenix（注意：如果hbase已经启动，需要重启第6个步骤的jar包才能加载进来）
bin/sqlline.py hadoop101,hadoop102,hadoop103:2181:/hbase
```

### 配置hbase支持Phoenix二级索引
- 步骤 1: 添加如下配置到 HBase 的 Hregionerver 节点的 hbase-site.xml
```
<!-- phoenix regionserver 配置参数，用于支持hbase二级索引 -->
<property>
    <name>hbase.regionserver.wal.codec</name>
    <value>org.apache.hadoop.hbase.regionserver.wal.IndexedWALEditCodec</value>
</property>

<property>
    <name>hbase.region.server.rpc.scheduler.factory.class</name>
    <value>org.apache.hadoop.hbase.ipc.PhoenixRpcSchedulerFactory</value>
<description>Factory to create the Phoenix RPC Scheduler that uses separate queues for index and metadata updates</description>
</property>

<property>
    <name>hbase.rpc.controllerfactory.class</name>
    <value>org.apache.hadoop.hbase.ipc.controller.ServerRpcControllerFactory</value>
    <description>Factory to create the Phoenix RPC Scheduler that uses separate queues for index and metadata updates</description>
</property>

```
- 步骤 2: 添加如下配置到 HBase 的 Hmaster 节点的 hbase-site.xml
```
<!-- phoenix 在 hbase master 配置参数 -->
<property>
    <name>hbase.master.loadbalancer.class</name>
    <value>org.apache.phoenix.hbase.index.balancer.IndexLoadBalancer</value>
</property>

<property>
    <name>hbase.coprocessor.master.classes</name>
    <value>org.apache.phoenix.hbase.index.master.IndexMasterObserver</value>
</property>

```