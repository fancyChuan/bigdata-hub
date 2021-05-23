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