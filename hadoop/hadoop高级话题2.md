## hadoop

资源：内存（决定任务的生死）、CPU决定任务的快慢

YARN如何对资源进行调度和隔离的
- 资源调度：RM将某个NM上的资源分配给任务
- 资源隔离：NM为任务提供相应的资源，同时保证资源具有独占性
- YARN允许用户配置每个节点上`可用的`物理内存资源(这里的可用是指yarn有权去隔离的物理资源)
- 【YARN中内存资源的调度和隔离】
```
（1）yarn.nodemanager.resource.memory-mb
表示该节点上YARN可使用的物理内存总量，默认是8192（MB），注意，如果你的节点内存资源不够8GB，则需要调减小这个值，而YARN不会智能的探测节点的物理内存总量。

（2）yarn.nodemanager.vmem-pmem-ratio
任务每使用1MB物理内存，最多可使用虚拟内存量，默认是2.1。

（3） yarn.nodemanager.pmem-check-enabled
是否启动一个线程检查每个任务正使用的物理内存量，如果任务超出分配值，则直接将其杀掉，默认是true。

（4） yarn.nodemanager.vmem-check-enabled
是否启动一个线程检查每个任务正使用的虚拟内存量，如果任务超出分配值，则直接将其杀掉，默认是true。

（5）yarn.scheduler.minimum-allocation-mb
单个任务可申请的最少物理内存量，默认是1024（MB），如果一个任务申请的物理内存量少于该值，则该对应的值改为这个数。

（6）yarn.scheduler.maximum-allocation-mb
单个任务可申请的最多物理内存量，默认是8192（MB）。【TODO】一台机子上没有这么多内存时，是否还能申请这么多资源？？能拆分吗？

```
> YARN未提供Cgroups内存隔离机制，而是使用线程监控的方式。因为cgroups缺乏灵活性，java进程创建的瞬间内存翻倍，cgroups会直接kill任务

- 【YARN中CPU资源的调度和隔离】
```
（1）yarn.nodemanager.resource.cpu-vcores
表示该节点上YARN可使用的虚拟CPU个数，默认是8，注意，目前推荐将该值设值为与物理CPU核数数目相同。如果你的节点CPU核数不够8个，则需要调减小这个值，而YARN不会智能的探测节点的物理CPU总数。

（2） yarn.scheduler.minimum-allocation-vcores
单个任务可申请的最小虚拟CPU个数，默认是1，如果一个任务申请的CPU个数少于该数，则该对应的值改为这个数。

（3）yarn.scheduler.maximum-allocation-vcores
单个任务可申请的最多虚拟CPU个数，默认是32。
```