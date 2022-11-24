## Spark

项目包说明
- learningSpark，spark基础知识，RDD
- mlWithSpark，

spark开发
- 搭建学习环境
- [spark-shell的使用](spark-shell和spark-submit实践.md)
- [SparkCore(RDD编程)](SparkCore-RDD编程.md)
- SparkSQL
- 结构化流StructedStreaming
- Spark Streaming
- spark优化
- 集群管理

> 模拟分布式计算：[simulation](spark3.0/src/main/scala/cn/fancychuan/spark3/simulation)
> - Driver：任务主程序，启动并获取分布式计算结果。1.连接执行者（网络通信），2.拆分数据 3. 分配计算任务 4.获取执行结果
> - Executor：真正执行计算的地方，执行者1
> - Executor2：真正执行计算的地方，执行者2
> - SubTask：类似于线程，被分配计算任务的地方
> - Task：数据和计算逻辑所在地方，需要由Driver拆分后才进行

参考资料

- 官方文档：[http://spark.apache.org/docs/2.3.3/](http://spark.apache.org/docs/2.3.3/)
- 《spark快速大数据分析》(learning spark)
- 《Machine Learning with Spark 2nd》 蔡立宇译
    - 中文名《spark机器学习 第2版》
    - [github地址](https://github.com/PacktPublishing/Machine-Learning-with-Spark-Second-Edition)
- 《Spark SQL入门与实践指南》纪涵
- 《Spark海量数据处理：技术详解与平台实战》范东来著

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
- ML 持久化：基于 DataFrame 的API 对ML 模型的存储和载入，以及Scala、Java、Python和R 语言下的Pipeline 操作提供了支持。
- MLlib 的R 语言支持：SparkR 支持MLlib 中泛化线性模型、朴素贝叶斯、K-均值聚类和生存回归（survival regression）的API。
- Python：PySpark 2.0 支持新的MLlib 算法，如LDA、泛化线性回归、高斯混合模型等。


spark2.x新特性主要体现在3方面：
- 性能优化（Tungsten项目）
    - 网络IO和磁盘IO经常解决性能理论值，而CPU的利用率却很难维持在一个很高的水平。Tungsten项目为压榨cpu和内存的性能极限而生
    - 阶段一
        - 内存管理与二进制处理：
            - 绕过JVM提供的安全内存托管系统，直接使用sun.misc.Unsafe包中的类，自主管理内存
            - 使用BytesToBytesMap来替换HashMap，很适合顺序扫描场景（顺序扫描对缓存非常友好）。在大数据量压力下BytesToBytesMap几乎没有性能衰减，而HashMap最终会被GC压垮
        - 缓存感知计算：在计算过程多次访问存储也，缓存才有意义
        - 代码生成：不依赖编译组件，在运行时直接生成高性能字节码发到JVM上运行
    - 阶段二：
        - 提升Catalyst优化器性能
        - 全阶段代码生成
        - 列式存储
- 接口优化（统一Dataset和DataFrame接口）
    - Dataset的目的是提供类型安全的编程接口，可以在应用程序运行之前就检查错误
    - Dataset可以通过将表达式和数据字段暴露给查询计划程序和Tungsten的快速内存编码来利用Catalyst优化器。
    - DataFrame是Dataset Untyped API 即：DataFrame=Dataset[Row]  (Dataset分为有类型和无类型两种)
- 新一代流处理技术：Structured Streaming与持续型应用
    - 流的作用非常强大，但流的一个关键点是很难被构建和维护
    - Spark Streaming在很多情况下都无法满足业务的需求，如晚到事件、状态持久化、分布式读写等
    - Spark在2.0中基于Spark SQL引擎统一了流处理与批处理接口（Spark 2.0不仅统一了DataFrame与Dataset接口，还统一了流处理与批处理接口）
    - Spark 2.2正式推出了可用于生产的Structured Streaming套件

> java.util.HashMap在大数据场景下的一些缺点
> - 使用对象作为建和值导致内存额外开销
> - 采用对缓存非常不友好的内存布局方式
> - 不能通过计算偏移量直接定位字段


spark3.x大杀器：Hydrogen项目
- 目标：将涉及数据预处理以及模型训练等整个流程深度地与这些机器学习、深度学习框架进行集成
- Spark面临两大挑战：数据交换与执行模型
- Hydrogen项目的关键词是融合，数据交换从数据边界的层面进行了融合，而执行引擎在执行逻辑上将两种不同的分布式计算理念进行了融合



Spark 2.3以后开始支持Spark on Kubernetes运行模式

