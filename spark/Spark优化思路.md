## Spark优化

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

