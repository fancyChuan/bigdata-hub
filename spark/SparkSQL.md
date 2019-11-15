## SparkSQL

spark用于处理结构化数据的一个模块，提供了除数据之外的额外信息，spark用这些元信息进行优化

Datasets/DataFrame/RDD之间的区别于联系
> DataFrame是一个分布式数据容器
- RDD是最底层的抽象，其他两个都是基于RDD做了更高级的封装，更加友好易用
- DataFrame的概念来源于python的pandas，比RDD多了表头，也叫SchemaRDD
    - 提升了执行效率、减少数据读取以及执行计划的优化，比如使用了优化器Catalyst优化（比如filter下推、裁剪等）
    - 跟hive类型，也支持嵌套数据结构（struct、array、map）
- Datasets是spark1.6版本以后提出，提供了强类型支持，是DataFrame的拓展（DataFrame只知道字段不知道字段的类型），是spark最新的数据抽象。
    - 也会经过优化器Catalyst优化
    - 可以和java bean结合，在编译时就能检查出其他两个在运行才会发现的异常（因为不仅知道字段，还知道字段的类型）
- RDD转为DataFrame是不可逆的，而转为Datasets是可逆的。DataFrame是Dataset的特例 DataFrame = Dataset[Row] Row就是数据结构信息
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


DataFrame/Datasets 结构化数据，是需要有结构的描述信息Schema，也就是元数据，创建方式参见 [SchemaTest.java](https://github.com/fancyChuan/bigdata-learn/blob/master/spark/src/main/java/learningSpark/sparkSQL/SchemaTest.java)

RDD与Datasets在序列化上的区别：
- RDD: 通过Java serialization 或 Kryo 序列化
- Datasets: 通过 Encoder 来序列化对象

Encoder 动态生成代码以便spark各种操作，并在执行计划中做优化？？Encoder也有点像是在指定Datasets中字段的类型，只不过不能使用传统的比如String，而是要用优化过的Encoders.String()

#### Datasets/DataFrame/RDD相互转换
- RDD转为Dataset：
    - 利用反射获取Schema信息
    - 指定Schema信息
        - 当java bean无法被使用时，需要指定，比如RDD的元素是字符串
        - 使用DataTypes创建Schema信息
- RDD转DataFrame  
```
testDF = rdd.toDF("col1", "col2")
```
- Dataset转DataFrame
```
testDF = testDS.toDF
```
- DataFrame转Dataset
```
testDS = testDF.as[Sechma]
```
- DataFrame/Dataset转RDD
```
val rdd1 = testDF.rdd
val rdd2 = testDS.rdd
```


#### 聚合操作
常用的聚合操作【java写法】
- 分组计数 df.groupBy("column").count().show()
    - 结果按指定字段排序 df.groupBy("column").count().sort("count") // 计数默认字段名为 "count"
    - 结果按指定字段排序 df.groupBy("column").count().sort(col("count").desc()) 
    - 字段重命名       df.groupBy("column").count().withColumnRenamed("count", "cnt").sort("cnt")
    - 字段重命名       df.groupBy("column").agg(count).sort("cnt")
    - 直接使用SQL解决： spark.sql("select column, count(1) cnt from df group by column order by cnt desc")
    
用于Row是无类型的自定义聚合操作
- 需要继承 UserDefinedAggregateFunction 抽象类并实现方法
- 步骤
    - 先定义**输入的数据**、**中间计算结果**和**最终结果** 的结构化信息
    - 实现 initialize() update() merge() evaluate() 几个函数
    - 注册使用
    
用于Row是有结构化的自定义聚合操作
- 继承 Aggregator 并实现方法

两种自定义聚合函数与RDD的aggregate操作的区别于联系

对比项  | 无类型 | 类型安全（类型） | RDD的聚合操作
--- | --- | --- | ---
继承类 | UserDefinedAggregateFunction | Aggregator |
初始值 | initialize() | zero() | new Tuple()
更新操作 | update() | reduce() | 实现接口
合并操作 | merge() | merge() | 实现接口
计算返回值 | evaluate() | finish() | 对结果RDD再操作


#### 数据源
SparkSQL支持多种数据源
- 基本的load/save操作
```
Dataset<Row> usersDF = spark.read().load("examples/src/main/resources/users.parquet");
usersDF.select("name", "favorite_color").write().save("namesAndFavColors.parquet");
```
- 手动指定读取数据源的类型(json, parquet, jdbc, orc, libsvm, csv, text)和选项
```
Dataset<Row> peopleDF = spark.read().format("json").load("examples/src/main/resources/people.json");
peopleDF.select("name", "age").write().format("parquet").save("namesAndAges.parquet");

Dataset<Row> peopleDFCsv = spark.read().format("csv")
    .option("sep", ";")
    .option("inferSchema", "true")
    .option("header", "true")
    .load("examples/src/main/resources/people.csv");

usersDF.write().format("orc")
    .option("orc.bloom.filter.columns", "favorite_color")
    .option("orc.dictionary.key.threshold", "1.0")
    .option("orc.column.encoding.direct", "name")
    .save("users_with_options.orc");
```
- 直接用SQL读取文件
```
Dataset<Row> sqlDF = spark.sql("SELECT * FROM parquet.`examples/src/main/resources/users.parquet`");
```

数据保存模式
