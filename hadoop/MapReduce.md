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
##### 3.1.1 切片和并行度决定机制 
- 并行度决定机制：
    - 数据块：HDFS上块的大小，比如128M
    - 数据切片：数据切片只是在逻辑上对输入进行分片

> 设置reduce的并行数 job.setNumReduceTasks(10);

![img](https://github.com/fancychuan/bigdata-learn/blob/master/hadoop/img/数据切片与MapTask并行度决定机制.png?raw=true)

> 第4点的理解：一个文件夹下有两个文件，一个300m一个100m，那么默认会切分为4个分片，启动4个MapTask。也就是说，对每个文件做分片

##### 3.1.2 job提交流程源码和切片机制
##### 3.1.3 FileInputFormat切片机制
##### 3.1.4 CombineTextInputFormat切片机制
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

##### 3.1.5 FileInputFormat实现类
FileInputFormat针对不同的文件格式（比如基于行的日志文件、二进制格式文件、数据库表等）会有不同的实现类，包括；TextInputFormat、KeyValueTextInputFormat、NLineInputFormat、CombineTextInputFormat等

- TextInputFormat 
    - 默认的实现类，按行读取每条记录
    - key是该行在整个文件的**起始字节偏移量**LongWritable 
    - value是这行的内容，不包含任何终止符Text
    - kv方法是LineRecordReader
- KeyValueTextInputFormat 
    - 每一行均为一条记录，被分隔符切分为key/value
    - 分隔符通过在驱动类中设置 conf.set(KeyValueLineRecordReaderKEY_VALUE_SEPERATOR, "/t") 默认为tab
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

##### 3.1.6 自定义InputFormat
步骤如下：
- 自定义一个类继承FileInputFormat
- 自顶一个一个类继承RecordReader，实现自定义的将数据转为key/value形式
- 示例 [SelfFileInputFormat.java](https://github.com/fancychuan/bigdata-learn/tree/master/hadoop/src/main/java/mrapps/fileinputformat/SelfFileInputFormat.java)

#### 3.2 MR详细工作流程
需要分组，将分组的需求转为排序的需求

全排序，因为大数据量，所以先选择局部排序（采用的是快速排序），之后进行归并排序（完成合并的同时完成排序）

![img](https://github.com/fancychuan/bigdata-learn/blob/master/hadoop/img/MapReduce详细工作流程1.png?raw=true)

![img](https://github.com/fancychuan/bigdata-learn/blob/master/hadoop/img/MapReduce详细工作流程2.png?raw=true)

流程详解：上面的流程图是MR最全工作流程，shuffle过程从第7步开始16步结束。具体shuffle过程如下：
- 1) MapTask收集我们的map()方法输出的kv对，放到内存缓冲区中
- 2）从内存缓冲区不断溢出本地磁盘文件，可能会溢出多个文件
- 3）多个溢出文件会被合并成大的溢出文件
- 4）在溢出过程及合并的过程中，都要调用Partitioner进行分区和针对key进行排序
- 5）ReduceTask根据自己的分区号，去各个MapTask机器上取相应的结果分区数据
- 6）ReduceTask会取到同一个分区的来自不同MapTask的结果文件，ReduceTask会将这些文件再进行合并（归并排序）
- 7）合并成大文件后，Shuffle的过程也就结束了，后面进入ReduceTask的逻辑运算过程（从文件中取出一个一个的键值对Group，调用用户自定义的reduce()方法）

注意：shuffle中缓冲区大小会影响MR效率，原则上缓冲区越大，磁盘IO次数越少，速度越快，可以通过io.sort.mb调整，默认是100M

相应的源码
```
context.write(k, NullWritable.get());
output.write(key, value);
collector.collect(key, value,partitioner.getPartition(key, value, partitions));
	HashPartitioner();
collect()
	close()
	collect.flush()
sortAndSpill()
	sort()   QuickSort
mergeParts();
	
collector.close();
```

#### 3.3 Shuffle机制
三次排序（如果有Combiner，就会有两次Combiner生效）

![img](https://github.com/fancychuan/bigdata-learn/blob/master/hadoop/img/shuffle机制.png?raw=true)

##### 3.3.1 Partition分区：
-所谓的分区就是标明了这条数据应该去到哪个ReduceTask

```
public class HashPartitioner<K, V> extends Partitioner<K, V> {
  public int getPartition(K key, V value, int numReduceTasks) {
    return (key.hashCode() & Integer.MAX_VALUE) % numReduceTasks; // 使用Integer.MAX_VALUE是为了防止出现负的hash
  }
}
```
- 自定义分区 参见 [SelfPartitioner.java](https://github.com/fancychuan/bigdata-learn/tree/master/hadoop/src/main/java/mrapps/partition/SelfPartitioner.java)
    - 继承Partitioner类并重写getPartition()方法
    - 在job驱动中，设置自定义Partitioner
    - 根据自定义分区的逻辑设置相应的ReduceTask数量
- 自定义分区数与ReduceTask数量的关系
    - 自定义分区数不能大于ReduceTask，否则会报错
    - 准确的说是，自定义分区号的最大值不能大于ReduceTask。比如只有4个分区号（0/1/2/4）ReduceTask为4个，那么也会报错
    - 自定义分区数小于ReduceTask可以运行，但是会造成ReduceTask空转不干活。比如4个分区号，但是有5个ReduceTask，那么其中一个ReduceTask不干活，输出空的结果文件
    - 分区号需要从0开始，逐一累加

##### 3.3.2 WritableComparable
排序是MR框架中最重要的操作之一。MapTask和ReduceTask都会对数据按照key进行排序，这是hadoop的默认行为，不管程序逻辑上是不是真的需要排序。所以有的时候我们也可以利用这种特性，把需要排序的内容设置到key的位置上
> 对于MapTask，它会将处理的结果暂时放到环形缓冲区中，当环形缓冲区使用率达到一定阈值后，再对缓冲区中的数据进行一次快速排序，并将这些有序数据溢写到磁盘上，而当数据处理完毕后，它会对磁盘上所有文件进行归并排序。

> 对于ReduceTask，它从每个MapTask上远程拷贝相应的数据文件，如果文件大小超过一定阈值，则溢写磁盘上，否则存储在内存中。如果磁盘上文件数目达到一定阈值，则进行一次归并排序以生成一个更大文件；如果内存中文件大小或者数目超过一定阈值，则进行一次合并后将数据溢写到磁盘上。当所有数据拷贝完毕后，ReduceTask统一对内存和磁盘上的所有数据进行一次归并排序。

默认排序是按照字典顺序排序，且采用的是快速排序

排序的分类：
- 部分排序：保证输出的每个文件内部有序
- 全排序：只设置一个TaskReduce，结果只有一个文件，但是这样效率很低
- 辅助排序（GroupingComparator）：在Reduce端对key进行排序。使用场景：key为bean对象时，让一个或多个字段相同的key进入同一个reduce方法
- 二次排序：在自定义排序中如果compareTo中的判断条件为两个即为二次排序

自定义排序Writable
- 实现WritableComparable接口重写compareTo方法（注意这个时候就不需要在实现Writable接口了）
- 参见 [FlowBeanComparable.java](https://github.com/fancychuan/bigdata-learn/tree/master/hadoop/src/main/java/mrapps/comparable/FlowBeanComparable.java)
- 实现区内排序 [PartitionSortApp.java](https://github.com/fancychuan/bigdata-learn/tree/master/hadoop/src/main/java/mrapps/comparable/PartitionSortApp.java)

##### 3.3.3 Combiner合并
概述：
- Combiner是MR程序中的另一个组件，意义是对每一个MapTask的输出进行局部汇总以减少网络传输量
- Combiner组件的父类是Reducer
- 跟Reducer的区别在于运行的位置：
    - Combiner在每一个MapTask所在的节点运行
    - Reducer接收全局所有Mapper的输出结果
- 使用要求
    - 不影响最终的业务逻辑（比如求平均值就不能使用Combiner）
    - Combiner输出的kv类型应该与Reducer输入的kv类型一致

自定义Combiner
- 自定义Combiner类并继承Reducer，重写reduce方法
- 在job驱动类中设置 job.setCombinerClass(WordcountCombiner.class);

##### 3.3.4 GroupingComparator分组（辅助排序）（二次排序）
MR中是先排序后分组，当我们需要统计每笔订单（一个订单会有多个商品）中金额最大的商品，这个时候就需要二次排序，也就是需要先分组后排序

参见 [UsingGroupingCompartorApp.java](https://github.com/fancychuan/bigdata-learn/tree/master/hadoop/src/main/java/mrapps/groupingcomparator/UsingGroupingCompartorApp.java)

#### 3.4 MapTask工作机制


#### 3.5 ReduceTask工作机制

#### 3.6 OutputFormat数据输出
OutputFormat是所有MR输出的基类，常用的有：
- 文本输出TextOutputFormat： 默认的输出格式，把每条记录写成文本行
- SequenceFileOutputFormat： 格式紧凑容易被压缩，常用将SequenceFileInputFormat的输出作为后续MR任务的输入

自定义OutputFormat
- 新建一个类继承FileOutputFormat并实现方法
- 新建一个类继承RecordWriter并实现方法
- Driver中设置 job.setOutputFormatClass()

#### 3.7 join多种应用
join场景下map和reduce的工作：
- Map端
    - 为来自不同表/文件的kv对打上标签以区分不同的来源
    - 用连接字段作为key，其余部分和新加的标签作为value
- Reduce端
    - 以连接字段作为key的分组已经完成，需要将每个分组中那些来源于不同文件的记录分开
    - 最后进行合并

##### Reduce Join
比如有两张表：
- t_order: id、pid、amount
- t_product：pid、pname
```
select id, a.pid, amount, pname
from t_order a join t_product b on a.pid=b.pid
```

##### Map Join
使用场景：适用于一张表很小，另一张表很大。小表直接读到内存中

优点：Map端缓存多张表，提前处理业务逻辑，这样增加map端业务，减少Reduce端数据的压力，尽可能小的减少数据倾斜

一般10-15M以内的文件可以使用map，hive默认使用map join的文件大小是25M

```
具体办法：采用DistributedCache
1）在Mapper的setup阶段，将文件读取到缓存集合中。
2）在驱动函数中加载缓存。
// 缓存普通文件到Task运行节点。
job.addCacheFile(new URI("file://e:/cache/pd.txt"));
```

#### 3.8 计数器应用
hadoop为每个作业维护若干个内置计数器以描述多项指标

比如我们要统计日志中字段长度大于11的行数，可以在map执行：
```
protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] items = value.toString().split(" ");
            if (items.length > 11) {
                context.write(value, NullWritable.get());
                context.getCounter("ETL", "True").increment(1);
            } else {
                context.getCounter("ETL", "False").increment(1);
            }
        }
```
结果从MR运行的日志中可以看到如下信息
```
2019-10-24 00:26:11,156  INFO [main] (Job.java:1385) - Counters: 17
	File System Counters
		FILE: Number of bytes read=3040557
		FILE: Number of bytes written=3277492
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
	Map-Reduce Framework
		Map input records=14619
		Map output records=13770
		Input split bytes=124
		Spilled Records=0
		Failed Shuffles=0
		Merged Map outputs=0
		GC time elapsed (ms)=0
		Total committed heap usage (bytes)=324534272
	ETL
		False=849
		True=13770
	File Input Format Counters 
		Bytes Read=3040376
	File Output Format Counters 
		Bytes Written=2993323
```

简单版ETL [SimpleETLApp.java](https://github.com/fancychuan/bigdata-learn/tree/master/hadoop/src/main/java/mrapps/counter/SimpleETLApp.java)

复杂版ETL [SimpleETLApp.java](https://github.com/fancychuan/bigdata-learn/tree/master/hadoop/src/main/java/mrapps/counter/SimpleETLApp.java)