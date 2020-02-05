## Spark优化

### Spark SQL 执行代价
计算sql代价，实现资源动态申请

思路：
- 静态分析得到sql的查询区间、查询的表，然后查看元数据，得到相应的hdfs的大小
- 把etl任务和只读的任务分开，通过拓展spark的catalog和Analyzer可以实现

#### 数据分区
在分布式程序中，通信的代价是很大的，因此控制数据分布以获得最小的网络传输可以极大的提升整体性能。Spark程序可以通过控制RDD分区方式来减少通信开销。

分区并不是对所有的应用都有好处，RDD只需要被扫描一次就没必要预先进行分区处理。

Spark中所有的PairRDD都可以进行分区操作，通过哈希算法把相同的key放在同一个节点上

join操作的执行细节：
- 先求出两个RDD上的所有键的哈希值
- 把哈希值相同的元素通过网络传到同一台机器X上
- 在机器X上对相同key的元素进行连接操作

优化思路：
- 对大数据集X先进行分区，后面小的数据集x就可以利用X的分区信息进行join操作，性能更好
```
val userData = sc.sequenceFile[UserID, UserInfo]("hdfs://...xxx").persist()

def processNewLogs() {
    val events = sc.sc.sequenceFile[UserID, UserInfo]("hdfs://...yyy")
    val joined = userData.join(events)
    ....
}

// 需要对userData优化为：
val userData = sc.sequenceFile[UserID, UserInfo]("hdfs://...xxx")
                    .partitionBy(new HashPartitioner(100)) // 构造100个分区 
                    .persist()
// userData先做了分区并且持久化后，后面的join操作，Spark知道了userData的分区方式，
// 因此只会对events进行数据混洗，把特定的UserID记录发送到userData对应的分区所在的哪台机器上
```
> python中不需要传HashPartitioner对象，只需要传分区数 rdd.partitionBy(100)



#### 函数使用的优化
- rdd.groupByKey().mapValues(value -> value.reduce(func)) 这类型的操作可以直接用reduceByKey()代替，更为高效

### 内存、GC、数据结构方面的调优
优化内存使用主要有三方面考虑：
- 对象的内存占用量
- 访问这些数据的开销
- 垃圾回收的负载

##### spark的内存管理
spark的内存使用大部分属于两类：执行（用于计算）和存储（用于缓存和在集群中传播）

在spark内部，执行器和存储器共用内存。
- 当执行器需要使用内存时，可以驱逐存储器的占用，但是只有在存储器使用的空间超过阈值R的时候才会发生。因为R是M的子区域，R中的缓存是不允许被驱逐的。
- 反过来，存储器是不能驱逐执行器的内存的。

> 这种设计的好处：不使用缓存的应用可以使用全部空间；使用缓存的应用可以保留最小的存储空间R，其中的数据块不受驱逐

有两个相关的配置：
- spark.memory.fraction将M的大小表示为 （JVM堆内存-300MB）*75% 【spark2.2改为60%】。剩余的25%用于用户数据结构
- spark.memory.storageFraction表示R作为M的一部分，默认为0.5，R是M的一部分，其中缓存的块能免于被执行器驱逐

一般不需要修改，能够满足绝大多数场景

##### 调优数据结构
避免使用增加负担的java特性，比如基于指针的数据结构和包装对象
- 优先将数据结构设计为**数组和原始类型**，而不是标准的java或者Scala集合类，比如hashmap
- 尽可能避免使用有很多小对象和指针的嵌套结构
- 针对关键词，考虑使用数字ID或者枚举对象，而不是字符串
- RAM小于32G的话，设置JVM标志 -XX +UseCompressedOops使指针为4个字节而不是8字节

##### 垃圾回收调优
垃圾回收的成本与java对象数量成正比。
> 尝试其他技术之前，优先尝试使用序列化缓存，看是否对解决GC有帮助

- GC调优的第一步：收集关于垃圾回收发生的频率和GC花费的时间。如下。注意这些日志是在Executor节点的stdout文件中，而不是在driver日志中
```
java ... -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps ...
```
- 有用的几个步骤
    - 收集完信息，判断是否有太多次垃圾回收。加入full gc在一个task完成前触发多次，说明task的内存空间不足，需要加内存
    - 在gc统计信息中，如果老年代接近满了，可以考虑减少用于缓存的内存大小（减少spark.memory.fraction）。也可以考虑减少年轻代。
    - 如果有太多的minor gc，较少的major gc。增加Eden区的大小会有帮助。如果Eden的大小为E那么新生代的内存可以设置为 -Xmn=4/3E
    - 尝试通过设置 -XX:+UseG1GC垃圾回收器为G1。对于大的Executor堆，通过使用-XX:G!HeapRegionSize去增大G1的对内存，显得尤为重要
    - 如果从HDFS读数据，解压缩块的大小通常为块大小的2-3倍。如果我们希望有3或4个任务的工作空间，而HDFS块为128MB，那么估计Eden的大小为4*3*128MB 
    - 监控垃圾回收的频率和时间如何随心设置的变化而变化