## Phoenix的使用

安装过程参见：[安装Phoenix](../环境搭建/hbase/安装Phoenix.md)

启动命令
```
bin/sqlline.py hadoop101,hadoop102,hadoop103:2181
```

#### 使用
##### hbase不存在表
- 创建表（创建的表会同步建到hbase中）
```
CREATE TABLE IF NOT EXISTS us_population (
      state CHAR(2) NOT NULL,
      city VARCHAR NOT NULL,
      population BIGINT
      CONSTRAINT my_pk PRIMARY KEY (state, city))
column_encoded_bytes=0;
```
> 主键映射到 HBase 中会成为 Rowkey. 如果有多个主键(联合主键), 会把多个主键的值拼成 rowkey

> 在 Phoenix 中, 默认会把表名,字段名等自动转换成大写. 如果要使用小写, 需要把他们用双引号括起来.

启动并创建表之后，在hbase中可以看到多了Phoenix的表
```
hbase(main):001:0> list
TABLE                                                                                                                                      
SYSTEM.CATALOG                                                                                                                             
SYSTEM.FUNCTION                                                                                                                            
SYSTEM.LOG                                                                                                                                 
SYSTEM.MUTEX                                                                                                                               
SYSTEM.SEQUENCE                                                                                                                            
SYSTEM.STATS                                                                                                                               
US_POPULATION                                                                                                                              
fruit                
...
```

- 插入数据
```
upsert into us_population values('NY','NewYork',8143197);
upsert into us_population values('CA','Los Angeles',3844829);
upsert into us_population values('IL','Chicago',2842518);
```
##### hbase表存在
需要映射先到Phoenix中，否则无法查看
- 方式1 视图映射，适用于只做查询的场景
```
create view "student" (
id varchar primary key,
"info"."name" varchar,
"info"."age" varchar);

drop view "student";
```
- 方式2 表映射，能进行增删改查
```
create table "student" (
id varchar primary key,
"info"."name" varchar,
"info"."age" varchar
) column_encoded_bytes=0;

upsert into "student" values('1008', 'phoenix', '66');

# 注意，这里的student，因为在hbase创建的时候是小写，那么在Phoenix使用的时候，需要加上双引号
# 否则，Phoenix会找不到表
```
> 添加column_encoded_bytes=0这个参数之后, 在 HBase 中添加的数据在 Phoenix 中也可以查询到. 否则查询不到

- 总结：
    - 相比于直接创建映射表，视图的查询效率会低， 原因是：创建映射表的时候，Phoenix 会在表中创建一些空的键值对，这些空键值对的存在可以用来提高查询效率。
    - 使用create table创建的关联表，如果对表进行了修改，源数据也会改变，同时如果关联表被删除，源表也会被删除。但是视图就不会，如果删除视图，源数据不会发生改变

#### 二级索引
HBase 里面只有 rowkey 作为一级索引， 如果要对库里的非 rowkey 字段进行数据检索和查询， 往往要通过 MapReduce/Spark 等分布式计算框架进行，硬件资源消耗和时间延迟都会比较高

从 0.94 版本开始, HBase 开始支持二级索引.
- 全局索引：创建后在hbase中会生成一个表专门储存索引
    - 适合多读少写的场景。因为每次写操作，不仅需要更新数据，还需要更新索引
    - 网络开销大，家中RegionServer的压力
- 本地索引：创建后在原表中新增一个列族，在列族储存索引信息
    - 使用多写少读的场景
```
# 全局索引
create index idx_name on user_1(name);
drop index idx_name on user_1;
# 本地索引
create local index idx_addr on user_1(addr);
```


案例测试：
- 准备数据
```
create table user_1(id varchar primary key, name varchar, addr varchar)

upsert into user_1 values ('1', 'zs', 'beijing');
upsert into user_1 values ('2', 'lisi', 'shanghai');
upsert into user_1 values ('3', 'ww', 'sz');
```
- 查看查询是否索引
```
0: jdbc:phoenix:hadoop101:2181> explain select * from user_1 where id='1';
+-----------------------------------------------------------------------------------------------+-----------------+----------------+------+
|                                             PLAN                                              | EST_BYTES_READ  | EST_ROWS_READ  | EST_ |
+-----------------------------------------------------------------------------------------------+-----------------+----------------+------+
| CLIENT 1-CHUNK 1 ROWS 205 BYTES PARALLEL 1-WAY ROUND ROBIN POINT LOOKUP ON 1 KEY OVER USER_1  | 205             | 1              | 0    |
+-----------------------------------------------------------------------------------------------+-----------------+----------------+------+
1 row selected (0.052 seconds)
0: jdbc:phoenix:hadoop101:2181> explain select * from user_1 where name='1';
+------------------------------------------------------------------+-----------------+----------------+--------------+
|                               PLAN                               | EST_BYTES_READ  | EST_ROWS_READ  | EST_INFO_TS  |
+------------------------------------------------------------------+-----------------+----------------+--------------+
| CLIENT 1-CHUNK PARALLEL 1-WAY ROUND ROBIN FULL SCAN OVER USER_1  | null            | null           | null         |
|     SERVER FILTER BY NAME = '1'                                  | null            | null           | null         |
+------------------------------------------------------------------+-----------------+----------------+--------------+
2 rows selected (0.033 seconds)
```
> 出现full scan表示全部列扫描，没有使用上索引
- 创建索引
```
0: jdbc:phoenix:hadoop101:2181> create index idx_name on user_1(name);
3 rows affected (6.366 seconds)
0: jdbc:phoenix:hadoop101:2181> explain select * from user_1 where name='1';
+------------------------------------------------------------------+-----------------+----------------+--------------+
|                               PLAN                               | EST_BYTES_READ  | EST_ROWS_READ  | EST_INFO_TS  |
+------------------------------------------------------------------+-----------------+----------------+--------------+
| CLIENT 1-CHUNK PARALLEL 1-WAY ROUND ROBIN FULL SCAN OVER USER_1  | null            | null           | null         |
|     SERVER FILTER BY NAME = '1'                                  | null            | null           | null         |
+------------------------------------------------------------------+-----------------+----------------+--------------+
2 rows selected (0.056 seconds)
0: jdbc:phoenix:hadoop101:2181> explain select name,addr from user_1 where name='1';
+------------------------------------------------------------------+-----------------+----------------+--------------+
|                               PLAN                               | EST_BYTES_READ  | EST_ROWS_READ  | EST_INFO_TS  |
+------------------------------------------------------------------+-----------------+----------------+--------------+
| CLIENT 1-CHUNK PARALLEL 1-WAY ROUND ROBIN FULL SCAN OVER USER_1  | null            | null           | null         |
|     SERVER FILTER BY NAME = '1'                                  | null            | null           | null         |
+------------------------------------------------------------------+-----------------+----------------+--------------+
2 rows selected (0.049 seconds)
0: jdbc:phoenix:hadoop101:2181> explain select name from user_1 where name='1';
+---------------------------------------------------------------------------+-----------------+----------------+--------------+
|                                   PLAN                                    | EST_BYTES_READ  | EST_ROWS_READ  | EST_INFO_TS  |
+---------------------------------------------------------------------------+-----------------+----------------+--------------+
| CLIENT 1-CHUNK PARALLEL 1-WAY ROUND ROBIN RANGE SCAN OVER IDX_NAME ['1']  | null            | null           | null         |
|     SERVER FILTER BY FIRST KEY ONLY                                       | null            | null           | null         |
+---------------------------------------------------------------------------+-----------------+----------------+--------------+
2 rows selected (0.049 seconds)
0: jdbc:phoenix:hadoop101:2181> explain select id,name from user_1 where name='1';
+---------------------------------------------------------------------------+-----------------+----------------+--------------+
|                                   PLAN                                    | EST_BYTES_READ  | EST_ROWS_READ  | EST_INFO_TS  |
+---------------------------------------------------------------------------+-----------------+----------------+--------------+
| CLIENT 1-CHUNK PARALLEL 1-WAY ROUND ROBIN RANGE SCAN OVER IDX_NAME ['1']  | null            | null           | null         |
|     SERVER FILTER BY FIRST KEY ONLY                                       | null            | null           | null         |
+---------------------------------------------------------------------------+-----------------+----------------+--------------+
2 rows selected (0.038 seconds)
```
> 可以发现如果select中出现没有加索引的的字段，那么就会full scan