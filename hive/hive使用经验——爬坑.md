## Hive使用经验

#### 1.配置hive支持

#### 2.ORC存储格式的表，在新增字段时，会有问题
表现：建了一个ORC表，插入一行数据，添加一列，修改数据，最后再查询数据。报以下错误
```Error: java.io.IOException: java.lang.ArrayIndexOutOfBoundsException: 9 (state=, code=0)```
使用的是下面的代码
```
use test;
drop  table if exists t1;
create  table t1(c1 int, c2 string)
clustered by(c1) into 8 buckets
stored as orc tblproperties ('transactional'='true');
insert into t1 values(1, 'aaa');
alter  table t1 add columns (c3 string) ;
insert into t1 values(2, 'ttt', 'xxx');
update t1 set c2='ccc' where c1=1;
select * from t1;
```
> 但是在cdh上测试，执行到update语句的时候并不能成

Hive 1.1.0上执行的，JIRA上说2.0.0修复了ORC表模式修改的问题

TODO：源码上是怎么导致的？



