

### DDL(Data Definition Language)
#### 1. Create/Drop/Alter/Use Database
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
#### 2. Create/Drop/Truncate Table


## DML