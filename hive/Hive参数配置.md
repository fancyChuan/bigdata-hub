## Hive参数配置

- 动态分区
```
set hive.exec.dynamic.partition=true;(可通过这个语句查看：set hive.exec.dynamic.partition;) 
set hive.exec.dynamic.partition.mode=nonstrict; 
SET hive.exec.max.dynamic.partitions=100000;(如果自动分区数大于这个参数，将会报错)
SET hive.exec.max.dynamic.partitions.pernode=100000;
```

- 禁用hive矢量执行：
```
set hive.vectorized.execution.enabled=false;
set hive.vectorized.execution.reduce.enabled=false;
set hive.vectorized.execution.reduce.groupby.enabled=false;
```
> 不禁用可能会到来报错：java.lang.ArrayIndexOutOfBoundsException: 28

- 配置hive支持update、delete操作
