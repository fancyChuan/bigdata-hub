## YARN资源调度

YARN架构：

![img](https://github.com/fancychuan/bigdata-learn/blob/master/hadoop/img/YARN架构.png?raw=true)

YARN工作机制：

![image](img/YARN工作机制.png)


作业提交全过程：
- 作业提交
- 作业初始化
- 任务分配
- 任务运行
- 进度和状态更新
> YARN中的任务将其进度和状态(包括counter)返回给应用管理器, 客户端每秒(通过mapreduce.client.progressmonitor.pollinterval设置)向应用管理器请求进度更新, 展示给用户。
- 作业完成

作业提交过程之MapReduce

![image](img/作业提交之MR过程.png)