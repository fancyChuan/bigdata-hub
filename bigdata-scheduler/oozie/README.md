## Oozie


#### Oozie调度原理
oozie的每个action job的调度通过一个只有map的MR job来启动，即oozie-launcher
- 用于加载真正作业的加载器
- 需要向yarn集群申请AM运行

> 当多个任务并发启动时，也就是有多个launcher启动，需要占用不少AM资源。在yarn的公平调度策略中，AM占用队列的资源默认最大为0.5，可能会在造成执行spark等任务时因为资源不足而阻塞


#### 问题与解决
1. oozie存在失去问题，默认与中国时间相差8个小时，修改方法如下：
```
# oozie-site.xml
<property>
    <name>oozie.processing.timezone</name>
    <value>GMT+0800</value>
</property>
```