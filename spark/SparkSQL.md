## SparkSQL

spark用于处理结构化数据的一个模块，提供了除数据之外的额外信息，spark用这些元信息进行优化



- 临时视图：df.createOrReplaceTempView("people") 是会话范围生效的，当前session失效视图也不可用
- 全局视图：df.createGlobalTempView("people") 被绑定到 global_temp 类似于库，通过global_temp.people调用
> 是整个application生效的，还是一直存在？？ 


DataFrame/Datasets 结构化数据，是需要有结构的描述信息的，也就是元数据

RDD与Datasets在序列化上的区别：
- RDD: 通过Java serialization 或 Kryo 序列化
- Datasets: 通过 Encoder 来序列化对象

Encoder 动态生成代码以便spark各种操作，并在执行计划中做优化？？

SparkSQL支持两种方法将RDDs转为Datasets：
- 利用反射推测出Schema信息
- 指定Schema信息
    - 当java bean无法被使用时，需要指定，比如RDD的元素是字符串
    - 使用DataTypes创建Schema信息


#### 聚合操作
用于Row是无类型的自定义聚合操作
- 需要继承 UserDefinedAggregateFunction 抽象类并实现方法
- 步骤
    - 先定义**输入的数据**、**中间计算结果**和**最终结果** 的结构化信息
    - 实现 initialize() update() merge() evaluate() 几个函数
    - 注册使用
    
用于Row是有结构化的自定义聚合操作
- 继承 Aggregator 并实现方法