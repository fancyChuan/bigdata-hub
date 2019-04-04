## Kafka

#### 概念
- broker：每个kafka实例server，多个broker组成kafka集群
- topic: 类似于表，kafka对消息保存时依据topic父类
    - partition，分区，相当于文件夹。理论上partition数越多，吞吐率越高。（但也不能太大，否则broker宕机时重新恢复很慢）。做负载均衡用
    - replication，副本
- producer
- consumer
    - consumer group 消费组，也需要用zookeeper管理：用来实现一个topic消息的广播和单播
    - 同一个消费者组不能同时消费同一个分区，可以消费不同的分区
- leader
- follower

#### 单机部署
```
host.name=s00
log.cleaner.enable=true
zookeeper.connect=s00:2181/kafka
log.dirs=/opt/modules/kafka_2.9.2-0.8.1/logs
```

#### 集群部署

#### 常用命令
```
# 1. 启动broker
bin/kafka-server-start.sh config/server.properties

# 2.1 创建topic
bin/kafka-topics.sh --create --replication-factor 1 --partitions 1 --topic test --zookeeper s00:2181/kafka
# 2.2 查看当前所有topic
bin/kafka-topics.sh --list --zookeeper s00:2181/kafka
# 2.3 删除topic
bin/kafka-topics.sh --delete --zookeeper s00:2181 --topic test
# 2.4 查看topic描述
bin/kafka-topics.sh --topic test --describe --zookeeper s00:2181/kafka

# 3. 新建consumer
bin/kafka-console-consumer.sh --zookeeper s00:2181/kafka --topic test

# 4. 新建producer 
bin/kafka-console-producer.sh --broker-list s00:9092 --topic test
```
注意：
1. --zookeeper s00:2181/kafka 需要些实际使用的znode，如果是 s00:2181 那么就是根znode
2. 单机模式下--replication-factor只能为1，--partitions 可以为1或者2
> TODO: 为什么先启动producer然后产生的消息在consumer启动后会收不到？

#### Kafka生产过程分析
- 写入方式
    - 消息被采用push模式推送到broker中，追加写入partition中，属于顺序写磁盘（比随机内存效率高，保证吞吐率）
- 分区
    - 
