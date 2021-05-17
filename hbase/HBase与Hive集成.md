## HBase与Hive集成

集成细节参见：[配置hbase与hive集成.md](../环境搭建/hbase/配置hbase与hive集成.md)

#### 使用示例
有两种方式：
- hbase表不存在，在hive创建管理表
```
CREATE TABLE dw.hive_hbase_emp_table_space1 (
empno int,
ename string,
job string,
deptno int)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES ("hbase.columns.mapping" = ":key,info:ename,info:job,info:deptno")
TBLPROPERTIES ("hbase.table.name" = "hbase_emp_table")
;

insert into dw.hive_hbase_emp_table values 
(1001, 'zhangsan', 'java', 233),
(1002, 'bigdata', 'java', 232),
(1003, 'lisi', 'java', 233),
(1004, 'hadoop', 'java', 234),
(1005, 'zhangsan', 'java', 235)
;
```

- hbase表存在，在hive中创建外部表
```
CREATE EXTERNAL TABLE dw.student (
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
