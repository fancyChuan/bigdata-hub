## Hive参数配置

- 动态分区
```
set hive.exec.dynamic.partition=true;(可通过这个语句查看：set hive.exec.dynamic.partition;) 
set hive.exec.dynamic.partition.mode=nonstrict; 
SET hive.exec.max.dynamic.partitions=100000;(如果自动分区数大于这个参数，将会报错)
SET hive.exec.max.dynamic.partitions.pernode=100000;
```