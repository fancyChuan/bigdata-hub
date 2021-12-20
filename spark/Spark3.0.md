## Spark3.0

### 1、SQL引擎：自适应查询框架 AQE（Adaptive Query Execution）

简单来说，自适应查询就是在运行时不断优化执行逻辑。
> Spark 3.0 版本之前，Spark 执行 SQL 是先确定 shuffle 分区数或者选择 Join 策略后，再按规划执行，过程中不够灵活；现在，在执行完部分的查询后，
> Spark 利用收集到结果的统计信息再对查询规划重新进行优化。这个优化的过程不是一次性的，而是随着查询会不断进行优化, 让整个查询优化变得更加灵活和自适应。这一改动让我们告别之前无休止的被动优化。

自适应查询 AQE 的“三板斧”：
- 动态合并 Shuffle 分区
- 动态调整 Join 策略
- 动态优化数据倾斜

#### 1.1 动态合并 Shuffle 分区


#### 1.2 动态调整join策略
SparkJoin 策略大致可以分三种，分别是 Broadcast Hash Join、Shuffle Hash Join 和 SortMerge Join。其中 Broadcast 通常是性能最好的，Spark 会在执行前选择合适的 Join 策略。


#### 1.4 动态优化数据倾斜


#### 1.5 AQE参数说明
```
#AQE开关
spark.sql.adaptive.enabled=true #默认false，为true时开启自适应查询，在运行过程中基于统计信息重新优化查询计划
spark.sql.adaptive.forceApply=true #默认false，自适应查询在没有shuffle或子查询时将不适用，设置为true将始终使用
spark.sql.adaptive.advisoryPartitionSizeInBytes=64M #默认64MB,开启自适应执行后每个分区的大小。合并小分区和分割倾斜分区都会用到这个参数

#开启合并shuffle分区
spark.sql.adaptive.coalescePartitions.enabled=true #当spark.sql.adaptive.enabled也开启时，合并相邻的shuffle分区，避免产生过多小task
spark.sql.adaptive.coalescePartitions.initialPartitionNum=200 #合并之前shuffle分区数的初始值，默认值是spark.sql.shuffle.partitions，可设置高一些
spark.sql.adaptive.coalescePartitions.minPartitionNum=20 #合并后的最小shuffle分区数。默认值是Spark集群的默认并行性
spark.sql.adaptive.maxNumPostShufflePartitions=500 #reduce分区最大值，默认500，可根据资源调整

#开启动态调整Join策略
spark.sql.adaptive.join.enabled=true #与spark.sql.adaptive.enabled都开启的话，开启AQE动态调整Join策略

#开启优化数据倾斜
spark.sql.adaptive.skewJoin.enabled=true #与spark.sql.adaptive.enabled都开启的话，开启AQE动态处理Join时数据倾斜
spark.sql.adaptive.skewedPartitionMaxSplits=5 #处理一个倾斜Partition的task个数上限，默认值为5；
spark.sql.adaptive.skewedPartitionRowCountThreshold=1000000 #倾斜Partition的行数下限，即行数低于该值的Partition不会被当作倾斜，默认值一千万
spark.sql.adaptive.skewedPartitionSizeThreshold=64M #倾斜Partition的大小下限，即大小小于该值的Partition不会被当做倾斜，默认值64M
spark.sql.adaptive.skewedPartitionFactor=5 #倾斜因子，默认为5。判断是否为倾斜的 Partition。如果一个分区(DataSize>64M*5) || (DataNum>(1000w*5)),则视为倾斜分区。
spark.shuffle.statistics.verbose=true #默认false，打开后MapStatus会采集每个partition条数信息，用于倾斜处理
```

### 2、动态资源分配
动态资源分配（Dynamic Resource Allocation）针对每个 Spark 应用的 Executor 数进行动态调整
> Spark 2.4 版本中 on Kubernetes 的动态资源并不完善，在 Spark 3.0 版本完善了 Spark on Kubernetes 的功能，其中就包括更灵敏的动态分配

动态分配简单的说就是"按需使用"，空闲的executor超过一定的时间就会被回收，需要时再重新申请。有几个需要注意的细节：
- 何时新增/移除 Executor
- Executor 数量的动态调整范围
- Executor 的增减频率
- Spark on Kubernetes 场景下，Executor 的 Pod 销毁后，它存储的中间计算数据如何访问

#### 参数说明
```
spark.dynamicAllocation.enabled=true #总开关，是否开启动态资源配置，根据工作负载来衡量是否应该增加或减少executor，默认false

spark.dynamicAllocation.shuffleTracking.enabled=true #spark3新增，之前没有官方支持的on k8s的Dynamic Resouce Allocation。启用shuffle文件跟踪，此配置不会回收保存了shuffle数据的executor

spark.dynamicAllocation.shuffleTracking.timeout #启用shuffleTracking时控制保存shuffle数据的executor超时时间，默认使用GC垃圾回收控制释放。如果有时候GC不及时，配置此参数后，即使executor上存在shuffle数据，也会被回收。暂未配置

spark.dynamicAllocation.minExecutors=1 #动态分配最小executor个数，在启动时就申请好的，默认0

spark.dynamicAllocation.maxExecutors=10 #动态分配最大executor个数，默认infinity

spark.dynamicAllocation.initialExecutors=2 #动态分配初始executor个数默认值=spark.dynamicAllocation.minExecutors

spark.dynamicAllocation.executorIdleTimeout=60s #当某个executor空闲超过这个设定值，就会被kill，默认60s

spark.dynamicAllocation.cachedExecutorIdleTimeout=240s #当某个缓存数据的executor空闲时间超过这个设定值，就会被kill，默认infinity

spark.dynamicAllocation.schedulerBacklogTimeout=3s #任务队列非空，资源不够，申请executor的时间间隔，默认1s（第一次申请）

spark.dynamicAllocation.sustainedSchedulerBacklogTimeout #同schedulerBacklogTimeout，是申请了新executor之后继续申请的间隔，默认=schedulerBacklogTimeout（第二次及之后）

spark.specution=true #开启推测执行，对长尾task，会在其他executor上启动相同task，先运行结束的作为结果
```
