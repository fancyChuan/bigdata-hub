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


#### 3. 关于metastore生效的问题
```
<property>
  <name>hive.metastore.uris</name>
  <value>thrift://hadoop101:9083</valu>
</property>
```
- 服务端通过```hive --service metastore```启动metastore服务，默认在9083端口
- 如果所在机器的hive-site.xml上没有上面面的配置，那么启动hiveserver2的时候就不会去连接metastore，这个时候hiveserver2进程内部会自己维护metastore相关的功能

也就是说：在没有配置metastore的thrift链接的时候，hiveserver2自己就会充当metastore的角色，自己完成与元数据的交互



#### 4. sql开发注意事项
1.使用row_number的时候，注意是否会出现统一条件下多条记录，是否需要改用rank。比如同一账户一天发起多比存款，那么算总的金额就需要用rank
2.做join操作，密切关注是否会有其中一张表有重复值，关注是否会发生数据扩散的问题
3.使用between的时候注意的



substr（concat（0000，x），-4）取后四位，不够的话拼接0

表设计要求：
1.尽可能不管怎么重跑，数据是一致的