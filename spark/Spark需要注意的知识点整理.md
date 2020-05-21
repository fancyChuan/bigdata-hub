## Spark需要注意的知识点整理

- spark切片数与分区数的关系
    - parallelize(seq, numSlices)的切片数与分区数的关系
    - textFile(inpath, minPartitions)分区数与最小分区数、默认分区数的关系
```
parallelize的切片数/分区数
默认并行度：conf.getInt("spark.default.parallelism", math.max(totalCoreCount.get(), 2))
textFile分区数 参数传入的是最小分区数，而实际上的分区数不一定是最小分区数
默认最小分区数： math.min(defaultParallelism, 2)
```