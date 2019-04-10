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


行动(action)操作
- 触发实际计算，向驱动器程序返回结果或者把结果写入外部系统
- 常用count()计数，take(num)取出数据，collect()获取整个RDD中的数据
- 当调用一个新的行动操作时，整个RDD都会从头开始计算。可以通过将中间结果持久化避免这种低效的行为
> collect()需要确保单台机器的内存放得下的时候才能使用

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

