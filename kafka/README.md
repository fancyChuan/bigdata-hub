## Kafka

#### 概念
- broker：每个kafka实例server，多个broker组成kafka集群
- topic: 类似于表，kafka对消息保存时依据topic父类
    - partition，分区，相当于文件夹。理论上partition数越多，吞吐率越高。（但也不能太大，否则broker宕机时重新恢复很慢）。做负载均衡用
    - replication，副本
- producer
- consumer
    - consumer group 消费组，也需要用zookeeper管理
    - 同一个消费者组不能同时消费同一个分区，可以消费不同的分区
- leader
- follower