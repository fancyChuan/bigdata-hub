## Oozie


#### Oozie调度原理
oozie的每个action job的调度通过一个只有map的MR job来启动，即oozie-launcher
- 用于加载真正作业的加载器
- 需要向yarn集群申请AM运行

> 当多个任务并发启动时，也就是有多个launcher启动，需要占用不少AM资源。在yarn的公平调度策略中，AM占用队列的资源默认最大为0.5，可能会在造成执行spark等任务时因为资源不足而阻塞

#### 常用命令
```
# 直接运行作业：
oozie job -oozie http://localhost:11000/oozie -config job.properties -run 
# 提交作业并让作业进入PREP状态:
oozie job -oozie http://localhost:11000/oozie -config job.properties -submit
# 执行已提交的作业:
oozie job -oozie http://localhost:11000/oozie -start jobID
# 杀死任务:
oozie job -oozie http://localhost:11000/oozie -kill jobID
# 重新运行任务:
oozie job -oozie http://localhost:11000/oozie -config job.properties -rerun jobID -D oozie.wf.rerun.failnodes=false
# 提交pig作业 
oozie pig -oozie http://localhost:11000/oozie -file pigScriptFile -config job.properties -X -param_file params
# 提交MR作业 
oozie mapreduce -oozie http://localhost:11000/oozie -config job.properties
```

#### 作业配置文件说明
- job.properties 是入口文件,定义了一个任务
- workflow.xml :定义 workflow的配置文件
- coordinator.xml:定义coordinator的配置文件


#### 问题与解决
1. oozie存在失去问题，默认与中国时间相差8个小时，修改方法如下：
```
# oozie-site.xml
<property>
    <name>oozie.processing.timezone</name>
    <value>GMT+0800</value>
</property>
```