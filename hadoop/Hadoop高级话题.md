## Hadoop高级话题

### 1. Hadoop架构介绍
- 分布式计算的要求：
    - 拓展性：机器增加，计算和存储能力应该线性增长
    - 容错性：一个节点失败，主要的计算进程本身不会失败或者受到不利影响
    - 可恢复性：如果作业或者其中一部分失败，不应该有数据丢失
> Hadoop精心的设计是如何满足上面的要求的？
- 传统的数据处理更多的关注更强的计算机，因此导致了超级计算机的出现。
- 新软件栈使用由普通计算机组成的集群，以分布式文件系统（块很大、数据冗余特性）为基础。

- HDFS不是通用的文件系统
    - HDFS与基础的文件系统（如ext3）是不同，HDFS是`基于块的文件系统`，其中的文件被分解成块，以此能够存储一个大于单个磁盘空间的文件
    - 为大批量作业而设计的，作业从头到尾顺序读取大文件，与需要寻找特定值的OLTP应用不同
    - 一次写入，多次读取。一旦文件写入HDFS，就无法修改内容，也不能用现有名称去覆盖文件

### 2. 配置Hadoop集群
- hadoop除了*-default.xml等默认配置文件、*-site.xml等用户自定义配置文件、*-env.sh等运行环境外，还有的其他配置文件
    - hadoop-metric.properties 配置Hadoop metrics
    - allocations.xml 配置公平调度器
    - capacity-scheduler.xml 配置容量调度器
    - include 和 exclude 文件：白名单、黑名单

- 配置文件的优先级
    - 代码中（如JobConf或Job对象中）设置的参数具有最高优先级
    - 命令行中设置的参数，次一级
    - 客户端的比如mapred-site.xml文件中配置的参数具有再次一级优先级【也就是我们自己的项目工程中的配置文件）
    - DataNode节点上的配置文件mapred-site.xml具有再再次一级的优先级
    - mapred-default.xml最低优先级
- 为了防止参数被客户端活着程序修改，可以加上final标签
```
<property>
<name>dfs.hosts.include</name>
<value>/etc/hadoop/conf/hosts.include</value>
<final>true</final>
</property>
```
- 配置Hadoop守护进程环境变量
    - Java HOME目录
    - 多种Hadoop日志文件的存储地址
    - DN、NN、RM以及其他hadoop守护进程的JVM选项.一般使用 xxxx_xxx_OPTS 参数来配置
```
# 比如设置守护进程的内存大小为4g
YARN_RESOURCEMANAGER_OPTS = -Xmx4g
HADOOP_NAMENODE_OPTS = -Xmx4g
HADOOP_DATANODE_OPTS = -Xmx4g
HADOOP_SECONDARYNAMENODE_OPTS = -Xmx4g
```




