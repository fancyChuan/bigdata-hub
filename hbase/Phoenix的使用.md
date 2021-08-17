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

> 在 Phoenix 中, 默认会把表名,字段名等自动转换成大写. 如果要使用消息, 需要把他们用双引号括起来.

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
```
> 添加column_encoded_bytes=0这个参数之后, 在 HBase 中添加的数据在 Phoenix 中也可以查询到. 否则查询不到

- 总结：
    - 相比于直接创建映射表，视图的查询效率会低， 原因是：创建映射表的时候，Phoenix 会在表中创建一些空的键值对，这些空键值对的存在可以用来提高查询效率。
    - 使用create table创建的关联表，如果对表进行了修改，源数据也会改变，同时如果关联表被删除，源表也会被删除。但是视图就不会，如果删除视图，源数据不会发生改变

#### 二级索引