## 常用命令

```
# 1. 启动broker（在后台运行）
bin/kafka-server-start.sh -daemon config/server.properties 

# 2.1 创建topic
bin/kafka-topics.sh --zookeeper hadoop101:2181/kafka --create --replication-factor 2 --partitions 1 --topic test 
# 2.2 查看当前所有topic
bin/kafka-topics.sh --zookeeper hadoop101:2181/kafka --list 
bin/kafka-topics.sh --zookeeper hadoop101:2181,hadoop102:2181,hadoop103:2181/kafka --list 
# 2.3 删除topic
bin/kafka-topics.sh --zookeeper hadoop101:2181/kafka --delete --topic test
[注意]需要server.properties中设置delete.topic.enable=true否则只是标记删除
# 2.4 查看topic描述
bin/kafka-topics.sh --zookeeper hadoop101:2181/kafka --topic test --describe 
# 2.5 修改topic的分区
bin/kafka-topics.sh --zookeeper hadoop101:2181/kafka --alter --topic test --partitions 6


# 3. 新建consumer
bin/kafka-console-consumer.sh --zookeeper hadoop101:2181/kafka --topic test
# 3.1 新建消费组成员，需要在config/consumer.properties中配置消费者组id
bin/kafka-console-consumer.sh --zookeeper hadoop101:2181/kafka --topic test --consumer.config config/consumer.properties
# 4. 新建producer 
bin/kafka-console-producer.sh --broker-list hadoop101:9092/kafka --topic test
```

注意：
- 1.--zookeeper hadoop101:2181/kafka 需要写实际使用的znode，如果是 hadoop101:2181 那么就是根znode
- 2.单机模式下--replication-factor只能为1，--partitions 可以为1或者2
> TODO: 为什么先启动producer然后产生的消息在consumer启动后会收不到？
- 3.创建topic有两种方式：
    - 自动创建：配置中通过auto.create.topics.enable属性，开启后往kafka写入一个不存在的topic时会自动创建
    - 手动通过kafka-topics.sh --create 创建
    