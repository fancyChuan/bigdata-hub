## Spark


### 1. RDD基础
RDD：对数据的抽象——弹性分布式数据集，每个RDD都被分为多个分区运行在不同节点上，可以包含python、java、scala中任意类型的对象，甚至是用户自定义对象。

RDD.persist()可以让RDD缓存下来

#### 1.1 创建RDD
有两种方式：1. 读取外部数据集 2. 在驱动器程序中对一个集合进行并行化
```
# python
sc.parallelize(["hello", "spark"])
# java
JavaRDD<String> lines = sc.parallelize(Arrays.asList("hello", "spark"))
# scala
val x = sc.parallelize(List("hello", "spark-shell"))
``` 
#### 1.2  RDD操作
转化(transformation)操作
- 返回一个新的RDD，进行的是惰性求值（读取文件sc.textFile()也是惰性的）
- 可以操作任意数量的输入RDD，比如rdd1.union(rdd2)
- Spark会使用谱系图（lineage graph）来记录不同RDD之间的关系

函数 | 说明 | 举例
--- | --- | ---
filter | 过滤出符合条件的元素 
map | 对每个元素执行传入的方法，一对一 | 求平方
flatMap | 对每个元素执行传入的方法，一对多 | 把字符串切分为单词
distinct | 去重，开销很大，需要通过网络把所有数据进行混洗
union | 并集，合并前有重复的元素合并后也有 | rdd1.union(rdd2)
intersection | 交集，会去除重复的元素，单个RDD内的重复元素也会移除。性能较差，需要混洗 | 
subtract | 差集，有需要混洗 | 
cartesian | 笛卡尔积，在考虑所有组合的时候有用 |
sample  | 采样 | 参见 Main.testSample()

行动(action)操作
- 触发实际计算，向驱动器程序返回结果或者把结果写入外部系统
- 常用count()计数，take(num)取出数据，collect()获取整个RDD中的数据
- 当调用一个新的行动操作时，整个RDD都会从头开始计算。可以通过将中间结果持久化避免这种低效的行为
> collect()需要确保单台机器的内存放得下的时候才能使用

函数 | 说明 | 举例
--- | --- | ---
reduce | 操作RDD中的两个元素，返回一个同类型的新元素，有点类似于执行窗口为2的函数 | 累加 rdd.reduce((a, b) -> a + b)
fold | 跟reduce类似，只是多了个初始值，用作为每个分区第一次调用时的结果 | rdd.fold(100, (a, b) -> a + b))
aggregate(zeroValue, seqOp, combOp) | seqOp在每个分区执行map操作，combOp是把seqOP操作后的结果执行fold操作 | 求均值
countByValue() | 各元素在RDD中出现的次数 |
take(num) | 取num个元素
top(num) | 取前num个元素 | 
takeOrdered(num, ordering) | 按照指定规则排序后取前num个元素 | 
takeSample | 采样
foreach(func) | |

#### 1.3 向Spark传递函数
大部分转化操作和一部分行动操作都需要依赖用户传递的函数来计算。有几个注意的地方：
- python和Scala会把函数所在的对象也序列化传出去，应注意使用局部变量来避免 参见：[passFunction.py](https://github.com/fancyChuan/bigdata-learn/blob/master/spark/src/main/python/helloSpark.py)
- 传递的函数中包含不能序列化的对象会报错

在java用于传递的函数需要实现org.apache.spark.api.java.function任一函数式接口
- 标准Java函数式接口

函数名 | 实现的方法 | 用途
--- | --- | ---
Function<T, R> | R call(T) | 接收1个输入返回一个输出，用于类似于map()和filter()等操作中
Function2<T1, T2, R> | R call(T1, T2) | 接收2个输入返回一个输出，用于类似于fold()和aggregate()等操作中
FlatMapFunction<T, R> | Iterator(R) call(T) | 接收一个输入返回任意多个输出，用于类似于flatMap()这样的操作

- 针对专门类型的函数接口
函数名 | 等价函数 | 用途
--- | --- | ---
DoubleFlatMapFunction<T> | Function<T, Iterator<Double>> | 用于rdd.flatMapToDouble()生成DoubleRDD
DoubleFunction<T> | Function<T, Double> | 用于rdd.mapToDouble()生成DoubleRDD
PairFlatMapFunction<T, K, V> | Function<T, Iterator<Tuple2<K, V>>> | 用于rdd.flatMapToPair()生成PairRDD<K,V>
PairMapFunction<T, K, V> | Function<T, Tuple2<K, V>> | 用于rdd.mapToPair()生成PairRDD<K,V>

#### 1.4 持久化（缓存）
- persist() 缓存，unpersist() 取消缓存
- 缓存级别： 类型定义在StorageLevel中
级别 | 含义解释
--- | ---
MEMORY_ONLY | 这是默认的持久化策略，使用cache()方法时，实际就是使用的这种持久化策略：使用未序列化的Java对象格式，将数据保存在内存中
MEMORY_ONLY_SER | 含义同MEMORY_ONLY，只是会对RDD中的数据进行序列化，更省内存，能避免频繁GC
MEMORY_AND_DISK | 内存存不下则溢写到磁盘
MEMORY_AND_DISK_SER | 内存存不下则溢写到磁盘，进行序列化
DISK_ONLY | 只放在磁盘
> 在存储级别后面加个 "_2" 可以把持久化数据存为两份

#### 1.5 键值对操作
PairRDD 键值对RDD，元素为Java或Scala中的Tuple2对象或者python中的元组
> Tuple2对象可以通过 _1  _2 来访问元素
创建PairRDD，有两种方式：
- 并行化初始化：Java使用sc.parallelizePairs() python可以直接使用sc.parallelize()
- 从其他RDD转化而来: rdd.mapToPair() python直接rdd.map()
> 注意在Java中没有二元组类型，需要使用scala.Tuple2()来创建

PairRDD的转化操作
函数名 | 作用
--- | ---
reduceByKey | 把相同的key汇总到一起进行reduce操作
groupByKey | 把相同key的value分组
combineByKey | 基于key进行聚合，功能特点跟aggregate很想
mapValues | 只对value执行操作
flatMapValues | 只对value操作，跟flatMap类似
keys() | 返回仅包含key的RDD
values() | 返回仅包含value的RDD
sortByKey() | 对元素按key排序
rdd1.subtractByKey(rdd2) | 删掉rdd1中与rdd2的key相同的元素
rdd1.join(rdd2) | 内连接
rdd1.rightOutJoin(rdd2) | 右外连接
rdd1.leftOutJoin(rdd2) | 左外连接
rdd1.cogroup(rdd2) | 将两个RDD具有相同key的value分组到一起

combineByKey(createCombiner, mergeValue, mergeCombiners, partitioner) 执行细节
- 执行过程
    - combineByKey() 会遍历分区中的所有数据
    - 在每个分区一旦遇到新元素，就会调用createCombiner() 函数来创建key所对应的累加器的初始值
    - 在分区中已经遇到过的元素，调用mergeValue() 把累加器对应的值合并
    - 对于存在多个分区的相同的key，调用mergeCombiners()方法合并各个分区的结果
- combinerByKey有多个参数对应聚合操作的各个阶段，非常适合用来解析聚合操作各个阶段的功能划分
- todo：最后一个参数分区？？ 
