## SparkSQL

spark用于处理结构化数据的一个模块，提供了除数据之外的额外信息，spark用这些元信息进行优化

Datasets/DataFrame/RDD之间的区别于联系
- RDD是最底层的抽象，其他两个都是基于RDD做了更高级的封装
- DataFrame的概念来源于python的pandas，比RDD多了表头，也叫SchemaRDD，也会使用优化器Catalyst优化
- Datasets是spark1.6版本以后提出，提供了强类型支持，并且会结果sparkSQL的优化器Catalyst优化。可以和java bean结合，在编译时能检查出其他两个在运行才会发现的异常
- RDD转为DataFrame是不可逆的，而转为Datasets是可逆的
```
// 创建Datasets并指定java bean
Encoder<Employee> employeeEncoder = Encoders.bean(Employee.class);
String path = "examples/src/main/resources/employees.json";
Dataset<Employee> ds = spark.read().json(path).as(employeeEncoder);
ds.show();
```

- 临时视图：df.createOrReplaceTempView("people") 是会话范围生效的，当前session失效视图也不可用
- 全局视图：df.createGlobalTempView("people") 被绑定到 global_temp 类似于库，通过global_temp.people调用
> 是整个application生效的，还是一直存在？？ 


DataFrame/Datasets 结构化数据，是需要有结构的描述信息的，也就是元数据

RDD与Datasets在序列化上的区别：
- RDD: 通过Java serialization 或 Kryo 序列化
- Datasets: 通过 Encoder 来序列化对象

Encoder 动态生成代码以便spark各种操作，并在执行计划中做优化？？Encoder也有点像是在指定Datasets中字段的类型，只不过不能使用传统的比如String，而是要用优化过的Encoders.String()

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
- 