### DDL(Data Definition Language)
- Create/Drop/Alter/Use Database
- Create/Drop/Truncate Table
- Alter Table/Partition/Column
- Create/Drop/Alter View
- Create/Drop/Alter Index
- Create/Drop Macro
- Create/Drop/Reload Function
- Create/Drop/Grant/Revoke Roles and Privileges
- Show
- Describe

#### 1. [create/Drop/Alter/Use Database](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-Create/Drop/Alter/UseDatabase)
- create database
```sql
CREATE (DATABASE|SCHEMA) [IF NOT EXISTS] database_name
  [COMMENT database_comment]
  [LOCATION hdfs_path]
  [WITH DBPROPERTIES (property_name=property_value, ...)];
```
在hive中DATABASE|SCHEMA是等效的，可以相互替代
- drop database
    - RESTRICT: 默认值，在数据库中存在表的时候会失败，但如果是手动复制到该数据库下的，因为元数据中并没有相关信息，所以照样可以删除成功
    - CASCADE: 不管是否库中有表，全部删除
```sql
DROP (DATABASE|SCHEMA) [IF EXISTS] database_name [RESTRICT|CASCADE];
```
- alter database
```sql
ALTER (DATABASE|SCHEMA) database_name SET DBPROPERTIES (property_name=property_value, ...);   -- (Note: SCHEMA added in Hive 0.14.0)
 
ALTER (DATABASE|SCHEMA) database_name SET OWNER [USER|ROLE] user_or_role;   -- (Note: Hive 0.13.0 and later; SCHEMA added in Hive 0.14.0)
  
ALTER (DATABASE|SCHEMA) database_name SET LOCATION hdfs_path; -- (Note: Hive 2.2.1, 2.4.0 and later)
```
#### 2. [Create/Drop/Truncate Table](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-CreateTableCreate/Drop/TruncateTable)
- create table
```sql
CREATE [TEMPORARY] [EXTERNAL] TABLE [IF NOT EXISTS] [db_name.]table_name    -- (Note: TEMPORARY available in Hive 0.14.0 and later)
  [(col_name data_type [COMMENT col_comment], ... [constraint_specification])]
  [COMMENT table_comment]
  [PARTITIONED BY (col_name data_type [COMMENT col_comment], ...)]
  [CLUSTERED BY (col_name, col_name, ...) [SORTED BY (col_name [ASC|DESC], ...)] INTO num_buckets BUCKETS]
  [SKEWED BY (col_name, col_name, ...)                  -- (Note: Available in Hive 0.10.0 and later)]
     ON ((col_value, col_value, ...), (col_value, col_value, ...), ...)
     [STORED AS DIRECTORIES]
  [
   [ROW FORMAT row_format] 
   [STORED AS file_format]
     | STORED BY 'storage.handler.class.name' [WITH SERDEPROPERTIES (...)]  -- (Note: Available in Hive 0.6.0 and later)
  ]
  [LOCATION hdfs_path]
  [TBLPROPERTIES (property_name=property_value, ...)]   -- (Note: Available in Hive 0.6.0 and later)
  [AS select_statement];   -- (Note: Available in Hive 0.5.0 and later; not supported for external tables)
 
CREATE [TEMPORARY] [EXTERNAL] TABLE [IF NOT EXISTS] [db_name.]table_name
  LIKE existing_table_or_view_name
  [LOCATION hdfs_path];
```
- drop table
```sql
DROP TABLE [IF EXISTS] table_name [PURGE];     -- (Note: PURGE available in Hive 0.14.0 and later)
```
- truncate table
```sql
TRUNCATE TABLE table_name [PARTITION partition_spec];
 
partition_spec:
  : (partition_column = partition_col_value, partition_column = partition_col_value, ...)
```
#### 3. [Alter table/partition/column](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-AlterTable/Partition/Column)
- alter table
```sql
# 重命名
ALTER TABLE table_name RENAME TO new_table_name;
# 修改表属性
ALTER TABLE table_name SET TBLPROPERTIES table_properties;
table_properties:
  : (property_name = property_value, property_name = property_value, ... )
# 修改表注释
ALTER TABLE table_name SET TBLPROPERTIES ('comment' = new_comment);
# 添加SerDe Properties
ALTER TABLE table_name [PARTITION partition_spec] SET SERDE serde_class_name [WITH SERDEPROPERTIES serde_properties];
ALTER TABLE table_name [PARTITION partition_spec] SET SERDEPROPERTIES serde_properties;
serde_properties:
  : (property_name = property_value, property_name = property_value, ... )
# 修改表存储属性
ALTER TABLE table_name CLUSTERED BY (col_name, col_name, ...) [SORTED BY (col_name, ...)]
  INTO num_buckets BUCKETS;
# 修改表倾斜信息
ALTER TABLE table_name SKEWED BY (col_name1, col_name2, ...)
  ON ([(col_name1_value, col_name2_value, ...) [, (col_name1_value, col_name2_value), ...]
  [STORED AS DIRECTORIES];
# 修改表为非倾斜
ALTER TABLE table_name NOT SKEWED;
# 修改表不以目录形式存储
ALTER TABLE table_name NOT STORED AS DIRECTORIES;
# 修改表倾斜字段位置信息
ALTER TABLE table_name SET SKEWED LOCATION (col_name1="location1" [, col_name2="location2", ...] );
# 修改表约束
ALTER TABLE table_name ADD CONSTRAINT constraint_name PRIMARY KEY (column, ...) DISABLE NOVALIDATE;
ALTER TABLE table_name ADD CONSTRAINT constraint_name FOREIGN KEY (column, ...) REFERENCES table_name(column, ...) DISABLE NOVALIDATE RELY;
ALTER TABLE table_name DROP CONSTRAINT constraint_name;
```
- alter partition
```sql
# 添加分区
ALTER TABLE table_name ADD [IF NOT EXISTS] PARTITION partition_spec [LOCATION 'location'][, PARTITION partition_spec [LOCATION 'location'], ...];
partition_spec:
  : (partition_column = partition_col_value, partition_column = partition_col_value, ...)
# 修改分区名
ALTER TABLE table_name PARTITION partition_spec RENAME TO PARTITION partition_spec;
# 表交换分区数据
-- 需要两张表表结构相同，且目标表分区不存在
-- Move partition from table_name_1 to table_name_2
ALTER TABLE table_name_2 EXCHANGE PARTITION (partition_spec) WITH TABLE table_name_1;
-- multiple partitions
ALTER TABLE table_name_2 EXCHANGE PARTITION (partition_spec, partition_spec2, ...) WITH TABLE table_name_1;
# 修复表分区信息
-- 一般在手动导入或者删除分区数据，而不是通过 alter add/drop partition 语句的时候
MSCK [REPAIR] TABLE table_name [ADD/DROP/SYNC PARTITIONS];
# 删除分区
ALTER TABLE table_name DROP [IF EXISTS] PARTITION partition_spec[, PARTITION partition_spec, ...]
  [IGNORE PROTECTION] [PURGE];            -- (Note: PURGE available in Hive 1.2.0 and later, IGNORE PROTECTION not available 2.0.0 and later)
# 归档/解归档分区文件 -- 只合并文件，不做压缩
ALTER TABLE table_name ARCHIVE PARTITION partition_spec;
ALTER TABLE table_name UNARCHIVE PARTITION partition_spec;
```
- Alter Either Table or Partition
```sql
# 修改文件格式
ALTER TABLE table_name [PARTITION partition_spec] SET FILEFORMAT file_format;
# 修改路径
ALTER TABLE table_name [PARTITION partition_spec] SET LOCATION "new location";
# 触发hook -- todo： 需要加深理解
ALTER TABLE table_name TOUCH [PARTITION partition_spec];
# 修改保护机制 -- CASCADE表示联动，即表和分区都允许/不允许被删
ALTER TABLE table_name [PARTITION partition_spec] ENABLE|DISABLE NO_DROP [CASCADE];
ALTER TABLE table_name [PARTITION partition_spec] ENABLE|DISABLE OFFLINE;
# 修改压缩方式
ALTER TABLE table_name [PARTITION (partition_key = 'partition_value' [, ...])]
  COMPACT 'compaction_type'[AND WAIT]
  [WITH OVERWRITE TBLPROPERTIES ("property"="value" [, ...])];
# 自动合并文件
ALTER TABLE table_name [PARTITION (partition_key = 'partition_value' [, ...])] CONCATENATE;
# Alter Table/Partition Update columns -- 没明白
ALTER TABLE table_name [PARTITION (partition_key = 'partition_value' [, ...])] UPDATE COLUMNS;
```
- Alter Column
```sql
# 修改列信息
ALTER TABLE table_name [PARTITION partition_spec] CHANGE [COLUMN] col_old_name col_new_name column_type
  [COMMENT col_comment] [FIRST|AFTER column_name] [CASCADE|RESTRICT];
# 添加/删除列
ALTER TABLE table_name 
  [PARTITION partition_spec]                 -- (Note: Hive 0.14.0 and later)
  ADD|REPLACE COLUMNS (col_name data_type [COMMENT col_comment], ...)
  [CASCADE|RESTRICT]                         -- (Note: Hive 1.1.0 and later)
# 部分分区规范
ALTER TABLE foo PARTITION (ds='2008-04-08', hr=12) CHANGE COLUMN dec_column_name dec_column_name DECIMAL(38,18);
```
#### 4. [Create/Drop/Alter View](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-Create/Drop/AlterView)
```sql
# 创建视图
CREATE VIEW [IF NOT EXISTS] [db_name.]view_name [(column_name [COMMENT column_comment], ...) ]
  [COMMENT view_comment]
  [TBLPROPERTIES (property_name = property_value, ...)]
  AS SELECT ...;
# 删除视图
DROP VIEW [IF EXISTS] [db_name.]view_name;
# 修改视图属性
ALTER VIEW [db_name.]view_name SET TBLPROPERTIES table_properties;
table_properties:
  : (property_name = property_value, property_name = property_value, ...)
# 修改视图的select语句
ALTER VIEW [db_name.]view_name AS select_statement;
```
#### 5. [Create/Drop/Alter Index](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-Create/Drop/AlterIndex)
```sql
# 创建索引
CREATE INDEX index_name
  ON TABLE base_table_name (col_name, ...)
  AS index_type
  [WITH DEFERRED REBUILD]
  [IDXPROPERTIES (property_name=property_value, ...)]
  [IN TABLE index_table_name]
  [
     [ ROW FORMAT ...] STORED AS ...
     | STORED BY ...
  ]
  [LOCATION hdfs_path]
  [TBLPROPERTIES (...)]
  [COMMENT "index comment"];
# 删除索引
DROP INDEX [IF EXISTS] index_name ON table_name;
# 修改索引
ALTER INDEX index_name ON table_name [PARTITION partition_spec] REBUILD;
```
#### 6. [Create/Drop Macro](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-Create/DropMacro)
```sql
CREATE TEMPORARY MACRO macro_name([col_name col_type, ...]) expression;

DROP TEMPORARY MACRO [IF EXISTS] macro_name;
```
#### 7. [Create/Drop/Reload Function](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-Create/Drop/ReloadFunction)
```sql
# 临时函数
CREATE TEMPORARY FUNCTION function_name AS class_name;
DROP TEMPORARY FUNCTION [IF EXISTS] function_name;
# 永久函数
CREATE FUNCTION [db_name.]function_name AS class_name
  [USING JAR|FILE|ARCHIVE 'file_uri' [, JAR|FILE|ARCHIVE 'file_uri'] ];
DROP FUNCTION [IF EXISTS] function_name;
RELOAD (FUNCTIONS|FUNCTION);
```
#### 8. [Create/Drop/Grant/Revoke Roles and Privileges](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-Create/Drop/Grant/RevokeRolesandPrivileges)
#### 9. [Show](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-Show)
#### 10. [Describe](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-Describe)
