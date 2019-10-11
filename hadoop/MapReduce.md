## MapReduce
### 1. MapReduce概述
![img](https://github.com/fancychuan/bigdata-learn/blob/master/hadoop/img/MapReduce核心编程思想.png?raw=true)

注意；
- map阶段的MapTask并发实例完全并行运行，互不干扰
- reduce节点也并发执行，互不相干，但是依赖map阶段的所有MapTask并发实例的输出（按分区）
- MapReduce编程模型只能包含一个Map阶段和一个Reduce阶段，如果用户的业务逻辑非常复杂，那就只能多个MapReduce程序，串行运行

一个完整的MapReduce程序在分布式运行时有三类实例进程：
- MRAppMaster： 负责整个程序的过程调度和状态协调
- MapTask： 负责map阶段的整个数据处理流程
- ReduceTask： 负责reduce阶段的整个数据处理流程

#### MapReduce编程规范
- Mapper阶段
    - 用户自定义的Mapper要继承自己的父类
    - Mapper的输入数据是KVa对的形式
    - Mapper的业务逻辑写在map()方法中
    - Mapper的输出数据是KV对的形式
    - map()方法对每一个KV对调用一次（在MapTask进程中）
- Reducer阶段
    - 业务逻辑写在reduce方法中
    - ReduceTask进程对每一组相同key的KV对调用一次reduce方法
- Driver阶段
    - 相当于YARN集群的客户端，用于提交整个程序到YARN集群
    - 提交的是封装了MapReduce程序相关运行参数的job对象

### 2. Hadoop序列化