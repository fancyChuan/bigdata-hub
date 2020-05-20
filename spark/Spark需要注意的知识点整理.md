## Spark需要注意的知识点整理

- spark切片数与分区数的关系
    - parallelize(seq, numSlices)的切片数与分区数的关系
    - textFile(inpath, minPartitions)分区数与最小分区数、默认分区数的关系