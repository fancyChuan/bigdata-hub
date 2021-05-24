## Flume使用示例


#### 1.监控端口数据官方案例

[flume-netcat-logger.conf](conf/flume-netcat-logger.conf)

启动命令
```
bin/flume-ng agent \
-n a1 \
-c /usr/local/flume/conf/ \
-f /home/appuser/forlearn/flumejob/flume-netcat-logger.conf \
-Dflume.root.logger=DEBUG,console
```




#### 2.实时读取本地文件到HDFS
前提条件：需要能够写数据到HDFS，因此需要hadoop的jar包，需要把以下的jar包放到flume的lib下，如果集群已经安装好了hadoop，那就不需要
```
commons-configuration-1.6.jar
hadoop-auth-2.7.2.jar
hadoop-common-2.7.2.jar
hadoop-hdfs-2.7.2.jar
commons-io-2.4.jar
htrace-core-3.1.0-incubating.jar
```

启动命令：
```
bin/flume-ng agent \
-c conf \
-n a2 \
-f /home/appuser/forlearn/flumejob/flume-file-hdfs.conf \
-Dflume.root.logger=DEBUG,console
```
> 让Flume存到HDFS的时候自动存为分区
> hdfs://hadoop101:8020/forlearn/flume/%Y%m%d/%H
> 要支持这种形式，需要在Event的header中有Timestamp，所以需要配置：
> hdfs.useLocalTimeStamp = true 

#### 3.

启动第3个agent：使用tail -f 不够稳定，严重依赖tail命令。改为：监控特定目录收集日志
```
bin/flume-ng agent \
-c conf \
-n a3 \
-f conf/flume-app.conf \
-Dflume.root.logger=DEBUG,console
```