## HBase与Hive集成

集成细节参见：[配置hbase与hive集成.md](../环境搭建/hbase/配置hbase与hive集成.md)

#### 使用示例
有两种方式：
- hbase表不存在，在hive创建管理表
> managed non-native
```
CREATE TABLE hive_hbase_emp_table (
empno int,
ename string,
job string,
deptno int)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES ("hbase.columns.mapping" = ":key,info:ename,info:job,info:deptno")
TBLPROPERTIES ("hbase.table.name" = "hbase_emp_table")
;
# 想让哪个字段做主键，就把:key放到哪个位置

# 开始写入数据。注意：这里写入实际数据是存在hbase里边的，但是hive和hbase能够同时查询得到
insert into hive_hbase_emp_table values 
(1001, 'zhangsan', 'java', 233),
(1002, 'bigdata', 'java', 232),
(1003, 'lisi', 'java', 233),
(1004, 'hadoop', 'java', 234),
(1005, 'zhangsan', 'java', 235)
;
```
> 貌似创建管理表无法指定namespace？似乎只能在HBase的default命名空间上


- hbase表存在，在hive中创建外部表
> external non-native
```
CREATE EXTERNAL TABLE student (
id int,
name string,
age int)
STORED BY 
'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES ("hbase.columns.mapping" = 
":key,info:name,info:age") 
TBLPROPERTIES ("hbase.table.name" = "student")
;

```

注意事项：
- 在建表的时候，hive中字段类型要和hbase中列的类型一致，以免转换出错


#### 相关理论
理论：
- StorageHandlers 
    - 是一个扩展模块，用于帮忙hive去处理没有存储在HDFS上的数据
    - 例如数据存储在hbase上，可以使用HBaseStorageHandler来让hive读写hbase的数据

- native table：本地表
    - hive不需要通过StorageHandlers 就能访问的表
    - hive建表的时候，建表语句使用：
```
[ROW FORMAT row format][STORED AS file_format]
``` 
- non-native table：非本地表
    - hive需要通过StorageHandlers 才能访问的表
    - 建表的时候使用：
```
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' [TBLPROPERTIES (...)]
```
- hive管理表
- hive外部表
- SerDe相关：序列化和反序列化器
    - 表中的数据是什么格式，就使用什么类型的SerDe
    - 普通文本数据，以及不指定SerDe的时候，默认使用LazySimpleSerDe
    - 常用的还有：JsnoSerDe、RegexSerDe等
