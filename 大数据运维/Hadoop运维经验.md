## Hadoop运维经验

#### 1. HDFS存储多目录
若HDFS存储空间紧张，需要对DataNode进行磁盘扩展

(1)修改hdfs-site.xml
```
<property>
    <name>dfs.datanode.data.dir</name>
<value>file:///${hadoop.tmp.dir}/dfs/data1,file:///hd2/dfs/data2,file:///hd3/dfs/data3,file:///hd4/dfs/data4</value>
</property>
```
(2)增加磁盘后，保证每个目录数据均衡
```
bin/start-balancer.sh –threshold 10
```
对于参数10，代表的是集群中各个节点的磁盘空间利用率相差不超过10%

> 开启之后会实时去监测，对集群有一定的压力，均衡完之后可以考虑关闭
```
bin/stop-balancer.sh
```

#### 2.配置hadoop集群支持LZO压缩配置
企业内用的多的是snappy和lzo两种压缩方式。而hadoop本身并不支持lzo压缩，需要自己根据集群的情况编译

(1)编译步骤：
```
Hadoop支持LZO

0. 环境准备
maven（下载安装，配置环境变量，修改sitting.xml加阿里云镜像）
gcc-c++
zlib-devel
autoconf
automake
libtool
通过yum安装即可，yum -y install gcc-c++ lzo-devel zlib-devel autoconf automake libtool

1. 下载、安装并编译LZO

wget http://www.oberhumer.com/opensource/lzo/download/lzo-2.10.tar.gz

tar -zxvf lzo-2.10.tar.gz

cd lzo-2.10

./configure -prefix=/usr/local/hadoop/lzo/

make

make install

2. 编译hadoop-lzo源码

2.1 下载hadoop-lzo的源码，下载地址：https://github.com/twitter/hadoop-lzo/archive/master.zip
2.2 解压之后，修改pom.xml
    <hadoop.current.version>2.7.2</hadoop.current.version>
2.3 声明两个临时环境变量
     export C_INCLUDE_PATH=/usr/local/hadoop/lzo/include
     export LIBRARY_PATH=/usr/local/hadoop/lzo/lib 
2.4 编译
    进入hadoop-lzo-master，执行maven编译命令
    mvn package -Dmaven.test.skip=true
2.5 进入target，hadoop-lzo-0.4.21-SNAPSHOT.jar 即编译成功的hadoop-lzo组件
```
(2)将编译好后的hadoop-lzo-0.4.20.jar 放入hadoop-2.7.2/share/hadoop/common/
```
cp hadoop-lzo-0.4.20.jar /usr/local/hadoop/share/hadoop/common/
# 分发到其他机器
xsync /usr/local/hadoop/share/hadoop/common/hadoop-lzo-0.4.20.jar
```
(3)core-site.xml增加配置支持LZO压缩
```
    <!--配置支持LZO压缩-->
    <property>
        <name>io.compression.codecs</name>
        <value>
            org.apache.hadoop.io.compress.GzipCodec,
            org.apache.hadoop.io.compress.DefaultCodec,
            org.apache.hadoop.io.compress.BZip2Codec,
            org.apache.hadoop.io.compress.SnappyCodec,
            com.hadoop.compression.lzo.LzoCodec,
            com.hadoop.compression.lzo.LzopCodec
        </value>
    </property>
    <property>
        <name>io.compression.codec.lzo.class</name>
        <value>com.hadoop.compression.lzo.LzoCodec</value>
    </property>
```
(4)创建lzo索引

LZO压缩文件的可切片特性依赖于其索引，故我们需要手动为LZO压缩文件创建索引
```
# 示例
hadoop jar /usr/local/hadoop/share/hadoop/common/hadoop-lzo-0.4.20.jar com.hadoop.compression.lzo.DistributedLzoIndexer /forlearn/shop-wh/bigtable.lzo
```


#### 3.hadoop参数调优
##### 3.1 HDFS参数调优hdfs-site.xml
dfs.namenode.handler.count=20 * log2(Cluster Size)，比如集群规模为8台时，此参数设置为60
> The number of Namenode RPC server threads that listen to requests from clients. If dfs.namenode.servicerpc-address is not configured then Namenode RPC server threads listen to requests from all nodes.
> NameNode有一个工作线程池，用来处理不同DataNode的并发心跳以及客户端并发的元数据操作。对于大集群或者有大量客户端的集群来说，通常需要增大参数dfs.namenode.handler.count的默认值10。设置该值的一般原则是将其设置为集群大小的自然对数乘以20，即20logN，N为集群大小


##### 3.2 YARN参数调优yarn-site.xml
- 情景描述：总共7台机器，每天几亿条数据，数据源->Flume->Kafka->HDFS->Hive
面临问题：数据统计主要用HiveSQL，没有数据倾斜，小文件已经做了合并处理，开启的JVM重用，而且IO没有阻塞，内存用了不到50%。但是还是跑的非常慢，而且数据量洪峰过来时，整个集群都会宕掉。基于这种情况有没有优化方案。
- 解决办法：内存利用率不够。这个一般是Yarn的2个配置造成的，单个任务可以申请的最大内存大小，和Hadoop单个节点可用内存大小。调节这两个参数能提高系统内存的利用率。
    - （a）yarn.nodemanager.resource.memory-mb 表示该节点上YARN可使用的物理内存总量，默认是8192（MB），注意，如果你的节点内存资源不够8GB，则需要调减小这个值，而YARN不会智能的探测节点的物理内存总量。
    - （b）yarn.scheduler.maximum-allocation-mb 单个任务可申请的最多物理内存量，默认是8192（MB）。
##### 3.3 Hadoop宕机
- 如果MR造成系统宕机。此时要控制Yarn同时运行的任务数，和每个任务申请的最大内存。调整参数：yarn.scheduler.maximum-allocation-mb（单个任务可申请的最多物理内存量，默认是8192M）
- 如果写入文件过量造成NameNode宕机。那么调高Kafka的存储大小，控制从Kafka到HDFS的写入速度。高峰期的时候用Kafka进行缓存，高峰期过去数据同步会自动跟上。


