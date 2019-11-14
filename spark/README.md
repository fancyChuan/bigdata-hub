## Spark

项目包说明
- learningSpark，spark基础知识，RDD
- mlWithSpark，

spark开发
- 搭建学习环境
- [SparkCore(RDD编程)](SparkCore-RDD编程.md)
- SparkSQL
- 结构化流StructedStreaming
- Spark Streaming
- spark优化
- 集群管理


参考资料

- 官方文档：[http://spark.apache.org/docs/2.3.3/](http://spark.apache.org/docs/2.3.3/)
- 《spark快速大数据分析》(learning spark)
- 《Machine Learning with Spark 2nd》 蔡立宇译
    - 中文名《spark机器学习 第2版》
    - [github地址](https://github.com/PacktPublishing/Machine-Learning-with-Spark-Second-Edition)


#### Spark2.0
TODO:需要加深理解
```
Spark 2.0 使用TE（Tungsten engine）。TE 借助现代编译器和MPP 数据库理念构建。它在运
行时输出优化后的字节码（bytecode），从而将查询转化为单个函数，避免了虚拟函数的调用。
TE 还使用CPU 寄存器来存储中间数据。
这种技术称为全阶段代码生成（whole stage code generation）。
```
Spark1.6到2.0
```
基于DataFrame 的API 将会成为主要的API
基于RDD 的API 开始进入以维护为主的模式
```
Spark2.0引入的新特性
- ML 持久化：基于DataFrame 的API 对ML 模型的存储和载入，以及Scala、Java、Python和R 语言下的Pipeline 操作提供了支持。
- MLlib 的R 语言支持：SparkR 支持MLlib 中泛化线性模型、朴素贝叶斯、K-均值聚类和生存回归（survival regression）的API。
- Python：PySpark 2.0 支持新的MLlib 算法，如LDA、泛化线性回归、高斯混合模型等。