

### 参考资料
1. [一文搞定数据仓库之拉链表，流水表，全量表，增量表](https://blog.csdn.net/mtj66/article/details/78019370)
2. [(2条消息)数据仓库中的拉链表（hive实现) - 大鹰的天空 - CSDN博客](https://blog.csdn.net/u014770372/article/details/77069518)


#### 几个by的区别对比
- ORDER BY 会对输入做全局排序，因此只有一个 Reduce（多个 Reduce 无法保证全局有序）会导致当输入规模较大时，需要较长的计算时间，
- Hive中指定了sort by，那么在每个reducer端都会做排序，也就是说保证了局部有序好处是：执行了局部排序之后可以为接下去的全局排序提高不少的效率（其实就是做一次归并排序就可以做到全局排序了。 
- ditribute by是控制map的输出在reducer是如何划分的（即输出到不同的文件里）。 
- cluster by的功能就是distribute by和sort by相结合（根据指定字段输出到不同文件里，然后对各个文件里的数据排序）
