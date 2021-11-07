## HBase
![image](HBase知识脑图.png)

关系型数据库的查询瓶颈：表的数据量达到几千万甚至几亿级别时，单条记录的检索将花费数秒甚至分钟级别，影响因素
- 高并发的更新
- 多表关联后的复杂查询以及频繁的group by 或者 order by

CAP原理
- Consistency（强一致性）：数据更新操作的一致性，所有数据变动都是同步的。
- Availability（高可用性）：良好的响应性能。
- Partition tolerance（高分区容错性）：可靠性。
> 任何分布式系统只能同时满足两点，无法三者兼顾

有些数据库在实现性能的同时会牺牲一部分一致性，即数据在更新时，不会立刻同步，而是经过了一段时间才达到一致性。这个特性也称之为**最终一致性**

### 1.HBase的特点
HBase是一种分布式、可扩展、支持海量数据存储的NoSQL数据库。面向列存储，构建与hadoop之上，提供对10亿级别表数据的快速随机实时读写。
- 海量存储
- 列式存储：指的是列族存储
- 极易扩展（RegionServer层面，以及HDFS层面）
- 高并发
- 稀疏：HBase的列具有灵活性，可以有任意多的列，在列数据为空时，不会占用存储空间

#### 1.1 HBase优点
HBase的优势主要在以下几方面：
- 海量数据存储
- 快速随机访问，高效访问
- 大量写操作的应用
- 高容错、高扩展

#### 1.2 HBase的缺点
- 架构设计复杂，且使用HDFS作为分布式存储，因此只是存储少量数据，它也不会很快。在大数据量时，它慢的不会很明显！
- Hbase不支持表的关联操作，因此数据分析是HBase的弱项。常见的 group by或order by只能通过编写MapReduce来实现！
- Hbase部分支持了ACID

#### 1.3 HBase使用场景
适合场景：单表超千万，上亿，且高并发！

不适合场景：主要需求是数据分析，比如做报表。数据量规模不大，对实时性要求高！

常见的应用场景：互联网搜索引擎数据存储（BigTable要解决的问题）
- 审计日志系统
- 实时系统
- 消息中心
- 内容服务系统

### 2.Hbase数据模型
#### 逻辑结构
![逻辑结构](images/HBase逻辑结构.png)

#### 物理存储结构
![image](images/HBase物理存储结构.png)

#### 数据模型
- **命名空间NameSpace**：类似于关系型数据库的database概念,自带2个命名空间hbase和default。hbase为内置的，而default为用户默认的命名空间
- **表和区域（Table&Region）**：当表随着记录数不断增加而变大后，会逐渐分裂成多份，成为区域，一个区域是对表的水平划分，不同的区域会被Master分配给相应的RegionServer进行管理
> HBase定义表时只需要声明列族即可，数据属性，比如超时时间（TTL），压缩算法（COMPRESSION）等，都在列族的定义中定义
- **列族（Column Family）**：表在水平方向有一个或者多个列族组成，一个列族中可以由任意多个列组成
    - 列族支持动态扩展，无需预先定义列的数量以及类型（HBase能够轻松应对字段变更的场景）
    - 所有列均以二进制格式存储，**用户需要自行进行类型转换**
    - 所有的列族成员的前缀是相同的，例如“abc:a1”和“abc:a2”两个列都属于abc这个列族
    - 列族存在的意义是HBase会把相同列族的列尽量放在同一台机器上
    - 官方建议一张表的列族定义的越少越好，列族太多会极大程度地降低数据库性能
> 表的相关属性大部分都定义在列族上，同一个表里的不同列族可以有完全不同的属性配置，但是同一个列族内的所有列都会有相同的属性
- **行（Row）**：由一个RowKey和多个Column（列）组成，一个行包含了多个列，这些列通过列族来分类
- **行健（Row Key）**：表的主键，表中的记录默认按照行健升序排序
- **时间戳（Timestamp）**：每次数据操作对应的时间戳，可以看作是数据的版本号
- **单元格（Cell）**：表存储数据的单元。由{行健，列（列族:标签），时间戳}唯一确定，其中的数据是没有类型的，都是以二进制的形式存储

### 3.HBase基本架构

![iamge](images/HBase架构.png)

基本概念：
- Master服务器：一般一个集群只有一个，服务维护表结构信息
    - 只负责各种协调工作（很像打杂），比如建表、删表、移动Region、合并等操作。这些操作的共性是都需要跨RegionServer
    - 对于RegionServer的操作：分配regions到每个RegionServer，监控每个RegionServer的状态，负载均衡和故障转移
- RegionServer服务器：可以有多个，负责存储数据的地方，保存的表数据直接存在HDFS上（调用了HDFS的客户端接口）
    - RigionServer对Region的操作：splitRegion、compactRegion
    - 对数据的操作：get, put, delete
> HBase很特殊，客户端是直连RegionServer的，也就是master挂了以后还能查询，也可以存储和删除，但是无法新建表
- Region：一段数据的集合，一个HBase的表一般拥有一个到多个Region
    - Region由一个表的若干行组成！在Region中行的排序按照行键（rowkey）字典排序
    - Region不能跨RegionSever，数据量大的时候，HBase会拆分Region
    - 进行均衡负载的时候，也可能会把Region从一台服务器转到另一台服务器上
    - Region由RegionServer进程管理，一个服务器可以有一个或多个Region
- Zookeeper
    - HBase通过Zookeeper来做master的高可用、RegionServer的监控、元数据的入口以及集群配置的维护等工作
    - 非常依赖zookeeper，zk管理了所有RegionServer的信息，包括具体的数据段存放在哪个RegionServer上
    - 客户端每次与HBase连接，都是先跟zk通信，然后再与相应的RegionServer通信
- HBase在列上实现了BigTable论文提到的压缩算法、内存操作和布隆过滤器。
- HBase的表能够作为MapReduce任务的输入和输出，
- 可以通过Java API来存取数据，也可以通过REST、Avro或者Thrift的API来访问。
- 严格来说，HBASE是面向列族的



#### HBase存储机制
hbase的数据是存在HDFS上的，存储位置为：
```
库       /hbase/data/库名
表       /hbase/data/库名/表名
region  /hbase/data/库名/表名/region名
列族     /hbase/data/库名/表名/region名/列族名
数据     以文件的形式存在 /hbase/data/库名/表名/region名/列族名/ 下
```



