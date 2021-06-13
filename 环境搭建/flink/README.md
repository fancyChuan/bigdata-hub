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
- 1.启动hadoop集群
- 2.启动yarn-session
```
bin/yarn-session.sh -n 2 -s 2 -jm 1024 -tm 1024 -nm test -d

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
bin/flink run  -m yarn-cluster -c com.atguigu.flink.app.BatchWcApp \
/ext/flink0503-1.0-SNAPSHOT.jar \   
--input /applog/flink/input.txt \ 
--output /applog/flink/output5.csv
```