## Flume

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

启动第1个agent
```
bin/flume-ng agent \
-c conf \
-n a1 \
-f conf/a1.conf \
-Dflume.root.logger=DEBUG,console
```

启动第2个agent
```
bin/flume-ng agent \
-c conf \
-n a2 \
-f conf/flume-tail.conf \
-Dflume.root.logger=DEBUG,console
```