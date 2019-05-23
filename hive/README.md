## Hive


### 优化
hive的大部分优化都跟数据倾斜有关系，分为join相关、join不相关两种方式

hive优化的几点思路：
- 是否可以从dw层获取数据，而不是自行到ods层加工
- 考虑是否真的需要扫描那么多分区，计算周指标就不需要去扫描一年的分区
- select 的时候尽量不用*，并且尽可能多加where条件，减少MR中需要处理和分发的数据量


join无关的优化：
- group by 引起的倾斜优化
    - 原因：group by操作会按照key进行混洗分发到Reduce处理，就有可能引起数据倾斜
    - 优化方法：加入下面的配置，这样Hive在数据倾斜的时候会进行负载均衡
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

大表join小表优化


set hive.auto.convert.join = false;