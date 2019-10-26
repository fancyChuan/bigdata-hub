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
    
优化方法：
- 数据输入
    - 合并小文件
    - 采用CombineTextInputFormat来作为输入，解决输入端大量小文件场景
- Map阶段
    - 减少溢写（spill）次数：通过调整io.sort.mb和sort.spill.percent参数值，增大出发溢写的内存上线
    - 减少合并（merge）次数：调整io.sort.factor参数，增大Merge的文件数目，减少merge次数
    - 不影响业务逻辑前提下使用Combiner减少IO
- Reduce阶段
    - 合理设置Map和Reduce数量
        - 不宜设置太多，容易导致map、reduce任务间竞争资源，造成处理超时
        - 不宜设置太少，容易导致Task等待，延长处理时间
    - 设置map、reduce共存。调整slowstart.completedmaps，使map运行到一定程度后reduce开始运行，减少reduce等待时间
    - 规避使用reduce。reduce在用于连接数据集的时候会产生大量的网络消耗
    - 合理设置reduce端的Buffer
        - 默认情况下，Buffer的数据达到阈值时会写入磁盘，然后reduce从磁盘读取所有数据
        - 调整mapreduce.input.buffer.percent默认为0.0。当值大于0.0时会保留指定比例的内存用于读Buffer助攻的数据直接给reduce使用，减少磁盘IO
        - 需要根据作业特点进行调整
- IO传输
    - 采用数据压缩的方式，减少网络IO的时间。安装Snappy和LZO压缩编码器
    - 使用SequenceFile二进制文件
- 数据倾斜问题（减少数据倾斜的方法）
    - 抽样和范围分区：通过对原始数据进行抽样得到的结果集来预设分区边界值
    - 自定义分区：根据背景知识进行自定义分区
    - 使用Combiner
    - 采用Map join，尽量避免Reduce join
- 常用的调优参数
