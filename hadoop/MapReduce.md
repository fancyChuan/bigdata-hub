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
要在MapReduce中使用自定义的类，要保证能够序列化。有两种：使用Serializable或hadoop自带的序列化机制Writable

如果要把自定义的bean放到key中传输，那么还需要实现Comparable接口，因为MR框架的shuffle要求key必须能够排序

为什么不用java自带的序列化？
> java序列化是一个重量级序列化框架（Serializable），一个对象被序列化之后会附带许多额外的信息（各种校验信息、header、继承体系等），不利于在网络中高效传输

### 3. MapReduce框架原理
#### 3.1 InputFormat数据输入
- 并行度决定机制：
    - 数据块：HDFS上块的大小，比如128M
    - 数据切片：数据切片只是在逻辑上对输入进行分片

![img](https://github.com/fancychuan/bigdata-learn/blob/master/hadoop/img/数据切片与MapTask并行度决定机制.png?raw=true)

> 第4点的理解：一个文件夹下有两个文件，一个300m一个100m，那么默认会切分为4个分片，启动4个MapTask。也就是说，对每个文件做分片

#### 3.2 job提交流程源码和切片机制
#### 3.3 FileInputFormat切片机制
#### 3.4 CombineTextInputFormat切片机制
- TextInputFormat切片机制是对任务按文件规划切片，不管文件多小都会是一个单独的切片，交给一个MapTask处理，这样会有大量的MapTask
- CombineTextInputFormat应用场景
    - 用于小文件过多的场景，可以将多个小文件从逻辑上规划到一个切片中，处理之后的多个小文件就可以交给一个MapTask处理
- 虚拟存储切片最大值设置
```
CombineTextInputFormat.setMaxInputSplitSize(job, 4194304);// 4m
```
- 切片机制


#### 3.5 