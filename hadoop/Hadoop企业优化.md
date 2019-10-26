## Hadoop企业优化

MR程序效率的瓶颈：
- 计算机资源：CPU、内存、磁盘健康、网络
- IO操作优化
    - 数据倾斜
    - map和reduce数量设置不合理
    - map运行时间太长，导致reduce等待过久
    - 小文件过多
    - 大象的不可分块的超大文件
    - Spill次数过多
    - Merge次数过多等
    
#### MR优化方法：
1. 数据输入
    - 合并小文件
    - 采用CombineTextInputFormat来作为输入，解决输入端大量小文件场景
2. Map阶段
   - 减少溢写（spill）次数：通过调整io.sort.mb和sort.spill.percent参数值，增大出发溢写的内存上线
   - 减少合并（merge）次数：调整io.sort.factor参数，增大Merge的文件数目，减少merge次数
   - 不影响业务逻辑前提下使用Combiner减少IO
3. Reduce阶段
   - 合理设置Map和Reduce数量
       - 不宜设置太多，容易导致map、reduce任务间竞争资源，造成处理超时
       - 不宜设置太少，容易导致Task等待，延长处理时间
   - 设置map、reduce共存。调整slowstart.completedmaps，使map运行到一定程度后reduce开始运行，减少reduce等待时间
   - 规避使用reduce。reduce在用于连接数据集的时候会产生大量的网络消耗
   - 合理设置reduce端的Buffer
       - 默认情况下，Buffer的数据达到阈值时会写入磁盘，然后reduce从磁盘读取所有数据
       - 调整mapreduce.input.buffer.percent默认为0.0。当值大于0.0时会保留指定比例的内存用于读Buffer助攻的数据直接给reduce使用，减少磁盘IO
       - 需要根据作业特点进行调整
4. IO传输
   - 采用数据压缩的方式，减少网络IO的时间。安装Snappy和LZO压缩编码器
   - 使用SequenceFile二进制文件
5. 数据倾斜问题（减少数据倾斜的方法）
   - 抽样和范围分区：通过对原始数据进行抽样得到的结果集来预设分区边界值
   - 自定义分区：根据背景知识进行自定义分区
   - 使用Combiner
   - 采用Map join，尽量避免Reduce join

6.常用的调优参数

资源相关参数
- 用户在自己的MR程序中设置即可生效的（mapred-default.xml）

配置参数 | 参数说明
--- | ---
mapreduce.map.memory.mb | 一个MapTask可使用的资源上限（单位:MB），默认为1024。如果MapTask实际使用的资源量超过该值，则会被强制杀死。
mapreduce.reduce.memory.mb | 一个ReduceTask可使用的资源上限（单位:MB），默认为1024。如果ReduceTask实际使用的资源量超过该值，则会被强制杀死。
mapreduce.map.cpu.vcores|每个MapTask可使用的最多cpu core数目，默认值: 1
mapreduce.reduce.cpu.vcores|每个ReduceTask可使用的最多cpu core数目，默认值: 1
mapreduce.reduce.shuffle.parallelcopies|每个Reduce去Map中取数据的并行数。默认值是5
mapreduce.reduce.shuffle.merge.percent|Buffer中的数据达到多少比例开始写入磁盘。默认值0.66
mapreduce.reduce.shuffle.input.buffer.percent|Buffer大小占Reduce可用内存的比例。默认值0.7
mapreduce.reduce.input.buffer.percent|指定多少比例的内存用来存放Buffer中的数据，默认值是0.0

- 应该在YARN启动之前就配置在服务器的配置文件中才能生效（yarn-default.xml）

配置参数 | 参数说明
--- | ---
yarn.scheduler.minimum-allocation-mb | 给应用程序Container分配的最小内存，默认值：1024
yarn.scheduler.maximum-allocation-mb | 给应用程序Container分配的最大内存，默认值：8192
yarn.scheduler.minimum-allocation-vcores | 每个Container申请的最小CPU核数，默认值：1
yarn.scheduler.maximum-allocation-vcores | 每个Container申请的最大CPU核数，默认值：32
yarn.nodemanager.resource.memory-mb | 给Containers分配的最大物理内存，默认值：8192

- Shuffle性能优化的关键参数，应在YARN启动之前就配置好（mapred-default.xml）

配置参数	| 参数说明
--- | ---
mapreduce.task.io.sort.mb   |	Shuffle的环形缓冲区大小，默认100m
mapreduce.map.sort.spill.percent  | 	环形缓冲区溢出的阈值，默认80%

容错相关参数（MR性能优化）

配置参数|参数说明
--- | ---
mapreduce.map.maxattempts	| 每个Map Task最大重试次数，一旦重试参数超过该值，则认为Map Task运行失败，默认值：4。
mapreduce.reduce.maxattempts	| 每个Reduce Task最大重试次数，一旦重试参数超过该值，则认为Map Task运行失败，默认值：4。
mapreduce.task.timeout	| Task超时时间，经常需要设置的一个参数，该参数表达的意思为：如果一个Task在一定时间内没有任何进入，即不会读取新的数据，也没有输出数据，则认为该Task处于Block状态，可能是卡住了，也许永远会卡住，为了防止因为用户程序永远Block住不退出，则强制设置了一个该超时时间（单位毫秒），默认是600000。如果你的程序对每条输入数据的处理时间过长（比如会访问数据库，通过网络拉取数据等），建议将该参数调大，该参数过小常出现的错误提示是“AttemptID:attempt_14267829456721_123456_m_000224_0 Timed out after 300 secsContainer killed by the ApplicationMaster.”。


#### HDFS小文件优化方法
1. 小文件的优化
    - 数据采集时就将小文件或小批数据合并成大文件再上传HDFS
    - 在业务处理前，在HDFS上使用MR程序对小文件进行合并
    - 在MR处理时，采用CombineTextInputFormat提高效率
2. HDFS小文件解决方案
    - Hadoop Archive。将小文件归档成一个HAR文件
    - SequenceFile。一些列二进制key/value组成，如果key为文件名、value为文件内容，可以将大批小文集合并成一个大文件
    - CombineTextInputFormat。用于将多个文件合并成一个单独的split。
    - 开启JVM重用。开启后会减少45%运行时间。通过mapreduce.job.jvm.numtasks设置，一般值在10-20之间