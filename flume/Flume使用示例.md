## Flume使用示例


#### 1.监控端口数据官方案例



启动命令
```
bin/flume-ng agent \
-n a1 \
-c /usr/local/flume/conf/ \
-f /home/appuser/forlearn/flumejob/flume-netcat-logger.conf \
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