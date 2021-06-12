## Kafka监控和管理安装

#### Monitor
上传jar包KafkaOffsetMonitor-assembly-0.4.6.jar到集群/usr/local/kafka/kafka-monitor目录下

新建一个启动脚本
vim start.sh
```
#!/bin/bash
java -cp  KafkaOffsetMonitor-assembly-0.4.6-SNAPSHOT.jar \
com.quantifind.kafka.offsetapp.OffsetGetterWeb \
--offsetStorage kafka \
--kafkaBrokers hadoop101:9092,hadoop102:9092,hadoop103:9092 \
--kafkaSecurityProtocol PLAINTEXT \
--zk hadoop101:2181,hadoop102:2181,hadoop103:2181/kafka \
--port 8086 \
--refresh 10.seconds \
--retain 2.days \
--dbName offsetapp_kafka &
```

#### Manager
1.上传压缩包kafka-manager-1.3.3.15.zip到集群

2.解压kafka-manager-1.3.3.15.zip 到 /opt/app/下
```
unzip kafka-manager-1.3.3.15.zip -d /opt/app/
```

3.修改配置文件
conf/application.conf
```
kafka-manager.zkhosts="hadoop101:2181,hadoop102:2181,hadoop103:2181/kafka"
```

4.启动
```
chmod +x bin/kafka-manager

bin/kafka-manager
```

注意：开启JMX的话，需要在启动kafka broker的时候加上JMX端口环境变量
```
JMX_PORT=9988 kafka-server-start.sh -daemon /usr/local/kafka/config/server.properties 
```