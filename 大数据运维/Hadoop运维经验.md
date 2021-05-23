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
