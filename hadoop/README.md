## hadoop

主要参考《hadoop权威指南》第4版，源码地址：[https://github.com/tomwhite/hadoop-book](https://github.com/tomwhite/hadoop-book)
### 一、MapReduce组件
#### 1.1 windows下运行MR程序
- 单机模式 
    - hadoop单机模式，配置了HADOOP_HOME以后，不需要做任何配置
    - 寻找对应版本的hadoop.dll 和 winutils.exe.各版本下载地址：[https://github.com/steveloughran/winutils](https://github.com/steveloughran/winutils)
    - 把hadoop.dll放到 C:\Windows\System32 下面， winutils.exe 放在$HADOOP_HOME/bin下面
    - idea向运行java程序一样运行、debug
> windows本地运行mr程序时(不提交到yarn,运行在jvm靠线程执行)，hadoop.dll防止报nativeio异常、winutils.exe没有的话报空指针异常。

- 伪分布式
参考资料：
1. [Win7 64位系统上Hadoop单机模式的安装及开发环境搭建 - 黎明踏浪号 - 博客园](https://www.cnblogs.com/benfly/p/8301588.html)
2. [Eclipse连接Hadoop分析的三种方式](https://my.oschina.net/OutOfMemory/blog/776772)
- 建议：在window本地运行单机模式，用于开发调试。在一个虚拟机上部署一个伪分布式，用于测试。再部署一个完全分布式的集群用于模拟生产环境

#### 1.2 打包成jar运行到伪/完全分布式
对于集群来说，我们本地开发MR程序的时候所使用的依赖，集群都有。因此我们打包的时候不需要把依赖也打包进来。只需要把运行的源码以及META-INF打包即可。

因此，在IDEA里面，应该使用自定义的Empty如下图：

![打包成jar](https://note.youdao.com/yws/public/resource/5e17f5b36496bcc3b31a11e0a08e527e/xmlnote/5B97445A4659441BA919F853798AC7D3/37700)

#### 1.3 MR介绍
MR作业(job)是客户端需要执行的一个工作单元：包括输入数据、MR程序和配置信息。

Hadoop将一个作业分成若干个任务(task)执行，一个任务失败了，yarn会将它分到一个不同的节点上重新调度运行。
- mapper
    - map任务的输出写到本地硬盘而不是HDFS，具有数据本地化优势
    - hadoop会把输入数据划分为等长的小数据块，称为分片，每个分片对应一个map任务
    - 为了运行效率高，一般分片的大小设置成跟HDFS块大小一样，一般为128MB 
> 为什么太大大小都不利于获得最佳性能？
> - 分片太大，一个HDFS块放不下，就会跨越数据块存储（比如两个块），而HDFS的任何一个节点都基本不可能同时存储这两个块。意味着就会发生网络传输
> - 分片太小，那么管理分片和构建任务的时间又会大大加长，甚至可能比执行时间还长
- reduer
    - reduce任务不具备数据本地化的优势
    - reduce任务的数量并不是由输入数据的大小决定，而是独立设置的
    - 如果有多个reduce任务，默认通过哈希函数来对map的输出数据进行分区，比如有3个reducer，那么每个map的输出都会放到三个分区中，每个reducer任务对应一个分区
    - 也就是说：每一个reduce都会获得所有map的一份数据，这个过程就成为shuffle 见下图
    - 调整shuffle混洗参数对作业总运行时间的影响非常大
    - 当数据处理可以完全并行（无序shuffle）时，可能会出现没有reduce任务的情况
    
    ![image](https://github.com/fancyChuan/bigdata-learn/blob/master/hadoop/img/多个reduce任务的数据流.png?raw=true)    
- combiner
    - 集群的可用带宽限制了mr作业的数量，因此应尽量避免map和reduce任务之间的数据传输
    - combiner函数作用于map任务的输出，在传到reducer之前先做数据做处理，比如找到最大值

#### 1.4 Hadoop Streaming
- 允许使用其他非java语言开发MR程序
- Hadoop Streaming使用Unix标准流作为程序之间的接口，非常适合文本处理
- 运行：hadoop命令不支持streaming，需要执行streaming的jar文件
```
hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-2.5.0-cdh5.3.6.jar \
-files xxx_map.rb, xxx_reduce.rb
-input input/* \
-output output \
-mapper xxx_map.rb
-combiner xxx_reduce.rb
-reducer xxx_reduce.rb
```
上面命令在集群中使用时，需要加 -files 用于将脚本传输到集群

- python版本 参见： mapreduce/src/main/python
```
# 本地调试，使用type命令 类似于Linux的cat
E:\JavaWorkshop\bigdata-learn\hadoop>type input\ncdc\sample.txt | python mapreduce\src\main\python\max_temperature_map.py
1950    +0000
1950    +0022
1950    -0011
1949    +0111
1949    +0078
E:\JavaWorkshop\bigdata-learn\hadoop>type input\ncdc\sample.txt | python mapreduce\src\main\python\max_temperature_map.py | sort | python mapreduce\src\main\python\max_temperature_reduce.py
1950    -0011
1949    +0078
# 集群上运行
```
> python体系下，作为Streaming的替代方案，Dumbo使MR接口更像python

    
    
    
    
    
#### 参考资料
1. [IDEA向hadoop集群提交MapReduce作业 - shirukai的博客 - CSDN博客](https://blog.csdn.net/shirukai/article/details/81021872)
