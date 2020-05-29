## Kafka
Kafka是一个分布式的基于发布/订阅模式的消息队列，主要应用于大数据实时处理领域
> 消息队列的两种模式：
> 1.点对点模式（1对1）：消费了之后删除
> 2.发布订阅模式（1对多）：数据会存储起来一般过段时间才会删除

#### 概念
- broker：每个kafka实例server，多个broker组成kafka集群，一个broker可以容纳多个topic
- topic: 类似于表，kafka对消息保存时依据topic父类。也可以理解为就是一个队列
- partition：分区，相当于文件夹，一个分区是一个有序的队列
> 理论上partition数越多，吞吐率越高。（但也不能太大，否则broker宕机时重新恢复很慢）。做负载均衡用
- replication：副本，保证节点挂了数据也不丢失。一个topic的每个分区都有若干个副本，一个leader和若干个follower
    - leader：每个分区多个副本的主。生产者发送数据的时候发送到leader，消费者消费的时候也是到leader去获取数据
    - follower：每个分区多个副本的从。主要从leader同步数据，leader故障时，follower接替工作
- producer：生产者，向broker发送数据
- consumer
    - 从broker消费数据
    - consumer group 消费组，也需要用zookeeper管理：用来实现一个topic消息的广播和单播
    - 同一个消费者组不能同时消费同一个分区，可以消费不同的分区
> 消费者组内每个消费者负责消费不同分区的数据，一个分区只能由一个消费者消费；消费者组之间互不影响。所有的消费者都属于某个消费者组，即消费者组是逻辑上的一个订阅者

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
# 3.1 新建消费组成员，需要在config/consumer.properties中配置消费者组id
bin/kafka-console-consumer.sh --zookeeper s00:2181/kafka --topic test --consumer.config config/consumer.properties
# 4. 新建producer 
bin/kafka-console-producer.sh --broker-list s00:9092 --topic test
```
注意：
1. --zookeeper s00:2181/kafka 需要些实际使用的znode，如果是 s00:2181 那么就是根znode
2. 单机模式下--replication-factor只能为1，--partitions 可以为1或者2
> TODO: 为什么先启动producer然后产生的消息在consumer启动后会收不到？

### Kafka工作流程分析
#### 1. Kafka生产过程分析
- 写入方式
    - 消息被采用push模式推送到broker中，追加写入partition中，属于顺序写磁盘（比随机内存效率高，保证吞吐率）
- 分区
    - 消息被发送到topic的分区中，offset在分区类有效且唯一
    - 分区的原因：
        - 方便集群拓展
        - 提高并发，因为可以以分区为单位读写
    - 分区的原则：
        - 指定了patition，则直接使用；
        - 未指定patition但指定key，通过对key的value进行hash出一个patition
        - patition和key都未指定，使用轮询选出一个patition
- 副本
    - 在replication之间选出一个leader
    - producer和consumer只与这个leader交互，其它replication作为follower从leader中复制数据
- 写入流程
    - 1.producer先从zookeeper的 "/brokers/.../state"节点找到该partition的leader
    - 2.producer将消息发送给该leader
    - 3.leader将消息写入本地log
    - 4.followers从leader pull消息，写入本地log后向leader发送ACK
    - 5.leader收到所有ISR中的replication的ACK后，增加HW（high watermark，最后commit 的offset）并向producer发送ACK

#### 2. broker保存信息
- 存储方式
    - 物理上把topic分成一个或多个patition（对应 server.properties 中的num.partitions=3配置）
    - 每个patition物理上对应一个文件夹（该文件夹存储该patition的所有消息和索引文件）
- 存储策略
    - 无论消息是否被消费，kafka都会保留所有消息。
    - 有两种策略可以删除旧数据：
        - 基于时间：log.retention.hours=168
        - 基于大小：log.retention.bytes=1073741824
- ZooKeeper存储结构
![image](https://github.com/fancyChuan/bigdata-learn/blob/master/kafka/ZooKeeper%E5%AD%98%E5%82%A8%E7%BB%93%E6%9E%84.jpeg?raw=true)
    - 重点关注consumer和broker
    - producer不在zk中注册，消费者在zk中注册
#### 3. Kafka消费过程分析
- 高级API
    - 不需要去自行去管理offset，系统通过zookeeper自行管理
    - 不需要管理分区，副本等情况，系统自动管理
    - 缺点：
        - 不能自行控制offset（对于某些特殊需求来说）
        - 不能细化控制如分区、副本、zk等
- 低级API
    - 能够开发者自己控制offset，想从哪里读取就从哪里读取。
    - 自行控制连接分区，对分区自定义进行负载均衡
    - 对zookeeper的依赖性降低（如：offset不一定非要靠zk存储，自行存储offset即可，比如存在文件或者内存中）
- 消费者组
    
- 消费方式
    - consumer采用pull（拉）模式从broker中读取数据
    > 对于Kafka而言，pull模式更合适，它可简化broker的设计，consumer可自主控制消费消息的速率，同时consumer可以自己控制消费方式——即可批量消费也可逐条消费
    
#### 4.API
- 创建生产者
    - 不带回调 [NewProducer.java](https://github.com/fancyChuan/bigdata-learn/blob/master/kafka/src/main/java/producer/NewProducer.java)
    - 带回调 [NewProducerCallback.java](https://github.com/fancyChuan/bigdata-learn/blob/master/kafka/src/main/java/producer/NewProducerCallback.java)
- 创建消费者
    - 高级API
    - 低级API，开发步骤
        - 根据指定分区从主体分区元数据中找到主副本 findLeader()
        - 获取分区最新的消费进度（offset）getLastOffset()
        - 从主副本中拉去分区的消息 run()
        - 识别主副本的变化，重试 findNewLeader()
        
#### 5. 拦截器
