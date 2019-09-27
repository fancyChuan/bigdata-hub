## HDFS

java接口
- 从Hadoop URL读取数据
    - java自带的URL可以读取url的信息，new URL("hdfs://host/path"").openStream()
    - 默认读取不到hdfs，需要通过 URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory()) 参见[URLCat.java](https://github.com/fancyChuan/bigdata-learn/blob/master/hadoop/src/main/java/hdfs/URLCat.java)
    - 每个JVM只能执行一次 setURLStreamHandlerFactory()
- 通过FileSystem API读取数据
    - 直接使用InputStream
    - 使用FSDataInputStream
- 写入数据
    - FSDataOutputStream
- 目录
    - mkdirs(Path p)
- 查询文件系统
    - 文件元数据： FileStatus，封装了文件长度、块大小、副本、修改时间、所有者、权限等信息
    - 列出文件: listStatus()
        - FileStatus[] listStatus(Path p) 
        - FileStatus[] listStatus(Path p, PathFilter filter) 
        - FileStatus[] listStatus(Path[] ps, PathFilter filter) 
        - Hadoop的FileUtil中stat2Paths()可以把FileStatus[]转为Path[]
    - 文件模式：可以使用通配符来寻找需要执行的文件
        - FileStatus[] globStatus(Path pathPattern)
        - FileStatus[] globStatus(Path pathPattern, PathFilter filter)
        - 支持的通配符跟Unix系统一直
    - PathFilter对象： 以编程的方式控制通配符
        - 实现PathFilter接口的accept() 方法
    - 删除数据： delete(Path f, boolean recursive) recursive=true时，非空目录及其内容会被永久性删除
    

#### 剖析文件写入

![img](https://github.com/fancychuan/bigdata-learn/blob/master/hadoop/img/HDFS的写数据流程.png?raw=true)

注意：
- 第1步请求NN上传文件，NN会做一系列检查，比如目标文件是否已经存在，父目录是否存在，是否有权限等
- 第2步返回成功了之后才开始请求上传第一个block

#### 剖析文件读取

![img](https://github.com/fancychuan/bigdata-learn/blob/master/hadoop/img/HDFS的读数据流程.png?raw=true)

### 5. NameNode和SecondaryNameNode
HDFS对数据的一致性和安全性要求高。

#### 5.1 NN和2NN工作机制
SecondaryNameNode：专门用于FsImage和Edits的合并
- 元信息需要在内存里也需要在磁盘中备份：FsImage
- 同步更新FsImage效率过低，引入Edits（只追加，效率高）。每当元数据有变化的时候，修改内存中的元数据并追加到Edits中
- 长时间使用Edits会导致文件过大，需要定期合并，故映入2ndNameNode
> 持有化技术：
> - Redis：加载高效，生成慢           <==> hadoop 2NN
> - RDB：生成快，加载慢，安全性略低     <==> FsImage
> - AOF：实时操作，安全性高。资源占用多  <==> edits.log

![img](https://github.com/fancychuan/bigdata-learn/blob/master/hadoop/img/NameNode工作机制.png?raw=true)

先更新文件、在更新内存。为了安全性考虑。不如更新了内存，然后断电，那么数据就丢失了

#### 5.2 FsImage和Edits的解析
#### 5.3 checkpoint时间设置
- hdfs-default.xml 默认2NN一小时执行一次checkpoint
```
<property>
  <name>dfs.namenode.checkpoint.period</name>
  <value>3600</value>
</property>
```
- 一分钟检查一次操作次数
- 当操作次数达到100万时，2NN执行一次
```
<property>
  <name>dfs.namenode.checkpoint.txns</name>
  <value>1000000</value>
<description>操作动作次数</description>
</property>
<property>
  <name>dfs.namenode.checkpoint.check.period</name>
  <value>60</value>
<description> 1分钟检查一次操作次数</description>
</property >
```
#### 5.4 NN故障处理
两种方法：
- 将2NN中的数据拷贝到NN存储数据的目录
```
# 1. kill -9 NameNode进程
# 2. 删除NN存储的数据（/data/hadoop/tmp/dfs/name）
rm -rf /data/hadoop/tmp/dfs/name/*
# 3. 拷贝2NN中数据到原NN存储数据的目录
scp -r beifeng@s02:/data/hadoop/tmp/dfs/namesecondary/* /data/hadoop/tmp/dfs/name/
# 4. 重新启动NameNode
sbin/hadoop-daemon.sh start namenode
```
- 使用-importCheckpoint选项启动NameNode守护进程，从而将SecondaryNameNode中数据拷贝到NameNode目录中
```
# 1. 修改hdfs-site.xml
<property>
  <name>dfs.namenode.checkpoint.period</name>
  <value>120</value>
</property>
<property>
  <name>dfs.namenode.name.dir</name>
  <value>/opt/module/hadoop-2.7.2/data/tmp/dfs/name</value>
</property>
# 2. kill -9 NameNode进程
# 3. 删除NameNode存储的数据（/opt/module/hadoop-2.7.2/data/tmp/dfs/name）
# 4. 如果SecondaryNameNode不和NameNode在一个主机节点上，需要将SecondaryNameNode存储数据的目录拷贝到NameNode存储数据的平级目录，并删除in_use.lock文件
scp -r atguigu@hadoop104:/opt/module/hadoop-2.7.2/data/tmp/dfs/namesecondary  atguigu@hadoop104:/opt/module/hadoop-2.7.2/data/tmp/dfs/

cd namesecondary
rm -rf in_use.lock
# 5. 导入检查点数据（等待一会ctrl+c结束掉）
bin/hdfs namenode -importCheckpoint
# 6. 启动NameNode
sbin/hadoop-daemon.sh start namenode
```
#### 5.5 安全模式
##### 5.5.1 概述
- 1.NameNode启动
> NameNode启动时，首先将镜像文件（Fsimage）载入内存，并执行编辑日志（Edits）中的各项操作。一旦在内存中成功建立文件系统元数据的映像，则创建一个新的Fsimage文件和一个空的编辑日志。此时，NameNode开始监听DataNode请求。这个过程期间，NameNode一直运行在安全模式，即NameNode的文件系统对于客户端来说是只读的。
- 2.DataNode启动
> 系统中的数据块的位置并不是由NameNode维护的，而是以块列表的形式存储在DataNode中。在系统的正常操作期间，NameNode会在内存中保留所有块位置的映射信息。在安全模式下，各个DataNode会向NameNode发送最新的块列表信息，NameNode了解到足够多的块位置信息之后，即可高效运行文件系统。
- 3.安全模式退出判断
> 如果满足“最小副本条件”，NameNode会在30秒钟之后就退出安全模式。所谓的最小副本条件指的是在整个文件系统中99.9%的块满足最小副本级别（默认值：dfs.replication.min=1）。在启动一个刚刚格式化的HDFS集群时，因为系统中还没有任何块，所以NameNode不会进入安全模式。
##### 5.5.2 基本语法
集群处于安全模式，不能执行重要操作（写操作）。集群启动完成后，自动退出安全模式
```
（1）bin/hdfs dfsadmin -safemode get		（功能描述：查看安全模式状态）
（2）bin/hdfs dfsadmin -safemode enter  	（功能描述：进入安全模式状态）
（3）bin/hdfs dfsadmin -safemode leave	（功能描述：离开安全模式状态）
（4）bin/hdfs dfsadmin -safemode wait	（功能描述：等待安全模式状态）
```
##### 5.5.3