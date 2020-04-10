

https://www.cnblogs.com/ITtangtang/p/7683028.html


- 矢量化查询
```
set hive.vectorized.execution.enabled = true; # CDH默认是开启的，需要时orc存储格式，但是有时候简单的查询反而会报错
```
> 报错样例：http://www.haoblogs.cn/blog/front/toDetail?id=21
> 解释：https://www.docs4dev.com/docs/zh/apache-hive/3.1.1/reference/Vectorized_Query_Execution.html

- map join
 ```
 # 报错信息
 FAILED: Execution Error, return code 3 from org.apache.hadoop.hive.ql.exec.mr.MapredLocalTask
 # 原因：小表加载到内存中造成内存溢出导致的。
 # 解决方法：
 set hive.auto.convert.join=false;关闭自动转化MapJoin，默认为true;
 # 其他可调试的地方
 1.调小hive.smalltable.filesize，默认是25000000（在2.0.0版本中）
 2. hive.mapjoin.localtask.max.memory.usage 调大到0.999
 3. set hive.ignore.mapjoin.hint=false; 关闭忽略mapjoin的hints
 ```
 > Hive0.7之前，需要使用hint提示 /*+ mapjoin(table) */才会执行MapJoin
 
 
 
 ### 参考资料
 1. [hive的几种优化](https://www.cnblogs.com/SpeakSoftlyLove/p/6063908.html)