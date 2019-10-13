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
输入 -> 切片 -> KV值 -> 
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
CombineTextInputFormat.setMaxInputSplitSize(job, 4194304);// 4m，也就是只要文件大于4m就会
```
- 切片机制，包括两部分
    - 虚拟存储过程
        - 将目录下的所有文件，跟设置的setMaxInputSplitSize值比较
        - 如果不大于设置的最大值，比如4m，那么逻辑上分为一块
        - 如果输入文件大于设置的最大值且小于2倍，那么以最大值切一块，剩下的如果超过最大值且不大于小大之2倍，那么剩下的文件平均且为2个虚拟储存库（防止出现太小切片）
        > setMaxInputSplitSize值为4M，输入文件大小为8.02M，则先逻辑上分成一个4M。剩余的大小为4.02M切为2.01M两个文件
    - 切片过程
        - 判断虚拟存储的文件大小是否大于setMaxInputSplitSize值，大于等于则单独形成一个切片。
        - 如果不大于则跟下一个虚拟存储文件进行合并直到总大小大于等于最大值，也就是几个小的虚拟存储文件块共同形成一个切片
> 有4个小文件大小分别为1.7M、5.1M、3.4M以及6.8M这四个小文件
> - 设置最大值为4M，那么虚拟存储后形成6个文件块：1.7M，（2.55M、2.55M），3.4M和（3.4M、3.4M） 最终会形成3个切片，大小分别为：（1.7+2.55）M，（2.55+3.4）M，（3.4+3.4）M
> - 设置最大值为10M，那么虚拟存储后还是4个文件块，但是最终形成2个切片：(1.7+5.1+3.4)M，6.8M

- 使用：在驱动类中增加如下代码
```
// 默认是TextInputFormat
job.setInputFormatClass(CombineTextInputFormat.class);
//虚拟存储切片最大值设置4m
CombineTextInputFormat.setMaxInputSplitSize(job, 4194304);
```

#### 3.5 FileInputFormat实现类
FileInputFormat针对不同的文件格式（比如基于行的日志文件、二进制格式文件、数据库表等）会有不同的实现类，包括；TextInputFormat、KeyValueTextInputFormat、NLineInputFormat、CombineTextInputFormat等

- TextInputFormat 
    - 默认的实现类，按行读取每条记录
    - key是该行在整个文件的**起始字节偏移量**LongWritable 
    - value是这行的内容，不包含任何终止符Text
    - kv方法是LineRecordReader
- KeyValueTextInputFormat 
    - 每一行均为一条记录，被分隔符切分为key/value
    - 分隔符通过在驱动类中设置 conf.set(KeyValueLineRecordReaderKEY_VALUE_SEPERATOR, "\t") 默认为tab
- NLineInputFormat
    - 代表每个map进程处理的InputSplit不再按block去划分，而是按照NlineInputFormat执行的行数来划分
    - 即输入文件的总行数/n=切片数，如果不整除，切片数=商+1
    - kv方法是LineRecordReader
- CombineTextInputFormat
    - kv方法是CombineFileRecordReader
- FixedLengthInputFormat
    - kv方法是FixedLengthRecordReader
- SequenceFileInputFormat
    - kv方法是SequenceFileRecordReader
