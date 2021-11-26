## Flink高级特性
- Broadcast
- Accumulator
- Distributed Cache


### Broadcast
Broadcast可以理解为一个公共的共享变量。可以把一个DataSet（数据集）广播出去，不同的Task在节点上都能够获取到它，这个数据集在每个节点上只会存在一份。
如果不使用Broadcast，则在各节点的每个Task中都需要复制一份DataSet数据集，比较浪费内存（也就是一个节点中可能会存在多份DataSet数据）

