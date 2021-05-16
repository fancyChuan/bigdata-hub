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




- 日志相关
```
# beeline设置客户端日志显示级别
--hiveconf hive.server2.logging.operation.level=NONE
# beeline设置客户端的查询结果不显示表名
--hiveconf hive.resultset.use.unique.column.names = false
```
> HiveServer2操作日志记录模式可供客户端在会话级别设置。 为此，hive.server2.logging.operation.enabled应设置为true。 其允许的值为： NONE：忽略任何日志记录。 EXECUTION：记录任务完成情况。 PERFORMANCE: 执行+性能日志。 VERBOSE：所有日志。
```
# 直接在控制台查看当前debug运行日志命令,方便定位错误
bin/hive -hiveconf hive.root.logger=DEBUG,console 
```