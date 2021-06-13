## Flink部署的搭建
使用1.10.0版本
#### 一、standalone模式
跟spark类似，独立部署，不依赖与hadoop集群
```
spark:  master      <==> slave    driver     <==> executor
flink:  jobmanager  <==> slave    jobmanager <==> taskmanager
```
安装步骤：
- 1.解压缩 tar -zxvf flink-1.10.0-bin-hadoop27-scala_2.11.tgz 
- 2.修改conf/flink-conf.yaml文件，配置master为s01
```
jobmanager.rpc.address: hadoop101
```
- 3.修改conf/slave文件，配置从节点
```
hadoop102
hadoop103
```
- 4.分发到另外的两台机器上
- 5.启动集群 bin/start-cluster.sh
- 6.查看网页 http://hadoop101:8081



#### 二、yarn模式
有两种模式：Session-Cluster和Per-Job-Cluster模式
##### Session-Cluster模式
在yarn中初始化一个flink集群，开辟指定的资源，以后提交任务都向这里提交。这个flink集群会常驻在yarn集群中，除非手工停止

所有作业共享Dispatcher和ResourceManager；共享资源；适合规模小执行时间短的作业

部署过程：
- 1.启动hadoop集群
- 2.启动yarn-session
```
bin/yarn-session.sh -n 2 -s 2 -jm 1024 -tm 1024 -nm flinktest -d

其中：
-n(--container)：TaskManager的数量。
-s(--slots)：	每个TaskManager的slot数量，默认一个slot一个core，默认每个taskmanager的slot的个数为1，有时可以多一些taskmanager，做冗余。
-jm：JobManager的内存（单位MB)。
-tm：每个taskmanager的内存（单位MB)。
-nm：yarn 的appName(现在yarn的ui上的名字)。 
-d：后台执行。
```
- 3.提交任务时
```
bin/flink run -c cn.fancychuan.scala.quickstart.DataStreamWcApp \
-p 2 /home/appuser/forlearn/flink/flink-1.0-SNAPSHOT.jar \
--host hadoop101 --port 7777
```

##### Per-Job-Cluster 模式
一个Job会对应一个集群，每提交一个作业会根据自身的情况，都会单独向yarn申请资源，直到作业执行完成。作业结束，创建的集群也会消失

独享Dispatcher和ResourceManager，按需接受资源申请；适合规模大长时间运行的作业

使用方式：
- 1.启动hadoop集群
- 2.不启动yarn-session，直接执行job，加上-m参数
```
bin/flink run -m yarn-cluster -c cn.fancychuan.scala.quickstart.DataStreamWcApp /home/appuser/forlearn/flink/flink-1.0-SNAPSHOT.jar --host hadoop101 --port 7777
```