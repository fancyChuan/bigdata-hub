## Kafka安装部署

#### 单机部署
```
host.name=s00
log.cleaner.enable=true
zookeeper.connect=s00:2181/kafka
log.dirs=/opt/modules/kafka_2.9.2-0.8.1/logs
```

#### 集群部署
- 0.下载安装包 http://kafka.apache.org/downloads.html
- 1.解压缩并创建一个软连接到/usr/local/kafka
```
tar -zxvf kafka_2.11-0.11.0.0.tgz -C /opt/app/

sudo ln -s /opt/app/kafka_2.11-0.11.0.2 /usr/local/kafka
```
- 2.创建logs目录并配置
```
cd /usr/local/kafka
mkdir logs
vim config/server.properties
```
> 配置的内容如下：
```
#broker的全局唯一编号，不能重复
broker.id=0
#删除topic功能使能
delete.topic.enable=true
#处理网络请求的线程数量
num.network.threads=3
#用来处理磁盘IO的现成数量
num.io.threads=8
#发送套接字的缓冲区大小
socket.send.buffer.bytes=102400
#接收套接字的缓冲区大小
socket.receive.buffer.bytes=102400
#请求套接字的缓冲区大小
socket.request.max.bytes=104857600
#kafka运行日志存放的路径	
log.dirs=/usr/local/kafka/logs
#topic在当前broker上的分区个数
num.partitions=1
#用来恢复和清理data下数据的线程数量
num.recovery.threads.per.data.dir=1
#segment文件保留的最长时间，超时将被删除
log.retention.hours=168
#配置连接Zookeeper集群地址
zookeeper.connect=hadoop102:2181,hadoop103:2181,hadoop104:2181
```
> 最终配置结果参考 [server.properties](./server.properties)

- 3.发放代码到其他机器，并修改brokerid
```
xsync kafka_2.11-0.11.0.2
# 将hadoop102和103的brokerid分别改成1,2
```

- 4.分别在三台机器上启动kafka
```
xcall /usr/local/kafka/bin/kafka-server-start.sh -daemon /usr/local/kafka/config/server.properties
```
- 5.关闭kafka
```
bin/kafka-server-stop.sh stop
```