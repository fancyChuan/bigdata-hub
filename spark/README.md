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
转化(transformation)操作：返回一个新的RDD，进行的是惰性求值，可以操作任意数量的输入RDD，比如rdd1.union(rdd2)

行动(action)动作：触发实际计算，向驱动器程序返回结果或者把结果写入外部系统