## Flink
Flink项目的理念是：“Apache Flink是为分布式、高性能、随时可用以及准确的流处理应用程序打造的开源流处理框架”

Apache Flink 是一个框架和分布式处理引擎，用于在无边界和有边界数据流上进行有状态的计算。Flink 能在所有常见集群环境中运行，并能以内存速度和任意规模进行计算
- 有界流
- 无界流

Flink的应用场景
- 实时推荐系统
- 复杂事件处理：比如车载传感器等实时故障检测
- 实时欺诈检测
- 实时数仓与ETL
- 流数据分析
- 实时报表分析

Flink的特点和优势
- 同时支持高吞吐、低延迟、高性能
    - Storm低吞吐
    - Spark Streaming也是高吞吐
- 支持事件事件（Event Time）概念
    - 目前大多数框架窗口计算使用的都是系统事件（ProcessTime）
- 支持有状态计算
    - 指算子的中间结果存在内存或文件系统中，下一个事件到来的时候继续计算
    - spark默认是无状态的，而flink默认是有状态的
- 支持高度灵活的窗口操作
    - Flink将窗口划分为基于Time、Count、Session以及Data-driven等类型的窗口操作
- 基于轻量级分布式快照（checkpoint）事件的容错
- 基于JVM实现独立的内存管理
- 保存点（Save Point）
    - 默认不开启，需要手动开启
    - 一般用于升级，checkpoint一般用于容错

流式计算框架的对比
 


#### Flink应用
- [基于flink-sql的实时流计算web平台](https://github.com/zhp8341/flink-streaming-platform-web)
 

1. 是什么
2. 什么时候用
3. 怎么用