## Flume
flume各发行版本和文档: [http://flume.apache.org/releases/index.html](http://flume.apache.org/releases/index.html)

架构
- Flume-ng只有一个角色的节点
- agent
    - source
    - channel
    - sink

Events
- Event是Flume数据传输的基本单元
- 以事件的形式将数据从源头传输到最终目的地
- Event由可选的header和载有数据的一个byte array构成


常用：
```
Usage: bin/flume-ng <command> [options]...

global options:
  --conf,-c <conf>          use configs in <conf> directory
  -Dproperty=value          sets a Java system property value
  
agent options:
  --name,-n <name>          the name of this agent (required)
  --conf-file,-f <file>     specify a config file (required if -z missing)
```



启动第2个agent
```
bin/flume-ng agent \
-c conf \
-n a2 \
-f conf/flume-tail.conf \
-Dflume.root.logger=DEBUG,console
```
> 让Flume存到HDFS的时候自动存为分区
> hdfs://s00:8020/user/beifeng/flume/applogs/%Y%m%d
> 要支持这种形式，需要在Event的header中有Timestamp，所以需要配置：
> hdfs.useLocalTimeStamp = true 


启动第3个agent：使用tail -f 不够稳定，严重依赖tail命令。改为：监控特定目录收集日志
```
bin/flume-ng agent \
-c conf \
-n a3 \
-f conf/flume-app.conf \
-Dflume.root.logger=DEBUG,console
```