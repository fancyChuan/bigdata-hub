## SparkSQL

spark用于处理结构化数据的一个模块，提供了除数据之外的额外信息，spark用这些元信息进行优化



- 临时视图：df.createOrReplaceTempView("people") 是会话范围生效的，当前session失效视图也不可用
- 全局视图：df.createGlobalTempView("people") 被绑定到 global_temp 类似于库，通过global_temp.people调用
> 是整个application生效的，还是一直存在？？ 
