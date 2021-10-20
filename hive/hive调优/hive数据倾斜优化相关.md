### 数据倾斜优化
hive的大部分优化都跟数据倾斜有关系，分为join相关、join不相关两种方式
```
数据倾斜在代码上可能出现的地方：
1、做join的时候
select ...
from table_A join table_B on a.seller=b.seller
卖家的二八原则，可能有些卖家有成百上千万的买家，在按卖家ID做关联的时候，会涉及到shuffle，就会出现数据倾斜

2、做group by的时候
```

hive优化的几点思路：
- 是否可以从dw层获取数据，而不是自行到ods层加工
- 考虑是否真的需要扫描那么多分区，计算周指标就不需要去扫描一年的分区
- select 的时候尽量不用*，并且尽可能多加where条件，减少MR中需要处理和分发的数据量


#### 1、join无关的优化：
- group by 引起的倾斜优化
    - 原因：group by操作会按照key进行混洗分发到Reduce处理，就有可能引起数据倾斜
    - 优化方法：加入下面的配置，这样Hive在数据倾斜的时候会进行负载均衡（生成的查询计划会有两个MR JOB）
      - 第一个MR job：map输出结果随机分布到reduce中，reduce做部分聚合，达到负载均衡的目的
      - 第二哥MR job：跟进预处理的结果按GroupBy Key分布到Reduce中，保证相同key在同一个reduce中
```
set hive.map.aggr = true
set hive.groupby.skewindata = true
```
- count(distinct)优化
    - Hive将会把Map阶段的输出全部分布到一个Reduce Task上，此时很容易引起性能问题
    - 优化方法：先group by再count
```
select  count(*) 
from (select user from some_table group by user) temp;
# todo： 是否也可以  select user, count(1) from some_table group by user
```

#### 2、大表join小表优化：使用mapjoin
- 显式调用：调用mapjoin hint `/*+mapjoin()*/`，比如
```SELECT　/*+MAPJOIN（y）*/ FROM src1 x JOIN src2 y ON x.key = y.key;```
- 根据文件大小自动将common join转为map join（在提案Hive-1642）
```
# 默认开启
set hive.auto.convert.join = true;
```
> 默认判断的大小是25M，可以通过参数hive.mapjoin.smalltable.filesize（0.11.0版本后是hive.auto.convert.join.noconditionaltask.size）来确定小表的大小是否满足条件（默认25MB）

> 注意的是，HDFS显示的文件大小是压缩后的大小，当实际加载到内存的时候，容量会增大很多，很多场景下可能会膨胀10倍

参考资料：[Hive Map Join 原理](https://cloud.tencent.com/developer/article/1481780)

#### 3、 大表join大表
join相对较小的那个表也超过1G，一般我们就不能用map join了，否则得不偿失。

解决思路：
- 方案1：转为mapjoin：通过限制行和列的方式来降低小表的数量。比如过滤掉大表中不存在的数据
```
from A  a
join (
        select B.seller_id 
        from B t1
        join (selct seller_id from A group by seller_id) t2 
        on t1.seller_id=t2.seller_id 
    ) b 
on a.seller_id=b.seller_id
```
- 方案2：join时使用case when
> 此种解决方案应用场景为：倾斜的值是明确的而且数量很少，比如null值引起的倾斜。其核心是将这些引起倾斜的值随机分发到Reduce，其主要核心逻辑在于join时对这些特殊值concat随机数，从而达到随机分发的目的

Hive已对此进行了优化，只需要设置参数skewinfo和skewjoin参数，不需要修改SQL代码，例如，由于table_B的值“0”和“1”引起了倾斜，只需作如下设置：
```
set hive.optimize.skewinfo=table_b:(seller_id)[("0")("1")];
set hive.optimize.skewjoin=true;
```
- 方案3：倍数B表，再取模join
  - （1）通用方案：建立一个numbers表，其值只有一列int行，比如从1到10（具体值可根据倾斜程度确定），然后放大B表10倍，再取模join。这种方案之前倾斜的值的倾斜程度会减少为原来的1/10，代价是B表也会膨胀N倍
  - （2）专用方案：不需要整张表都放大n倍，只需要将大卖家放大即可
    - ①建立一个临时表动态放每日最新的大卖家（比如dim_big_seller)
    - ②如果是大卖家则随机concat一个正整数，如果不是就不变
```
参考代码：
[大表join小表方案3-通用方案示例代码.sql](示例sql/大表join小表方案3-专用方案示例代码.sql)
[大表join小表方案3-专用方案示例代码.sql](示例sql/大表join小表方案3-专用方案示例代码.sql)
```
- 方案4：终极解决方案就是**动态一分为二**，即对倾斜的键值和不倾斜的键值分开处理，不倾斜的正常join即可，倾斜的把它们找出来然后做mapjoin，最后union all其结果即可
> 该方案最通用，自由度最高，但是对代码的更改也最大，甚至需要更改代码框架，可作为终极方案来使用

参考代码：[大表join大表方案4-动态一分为二示例代码.sql](示例sql/大表join大表方案4-动态一分为二示例代码.sql)


参考资料：[离线和实时大数据开发实战](https://weread.qq.com/web/reader/7e332cb05e45157e3d0ec59ke3632bd0222e369853df322)