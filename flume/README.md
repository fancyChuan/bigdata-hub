## Flume
flume各发行版本和文档: [http://flume.apache.org/releases/index.html](http://flume.apache.org/releases/index.html)

### 1.Flume基础架构
![iamge](images/Flume基础架构.png)

- Flume-ng只有一个agent的节点
- Agent：Agent是一个JVM进程，它以事件的形式将数据从源头送至目的
- Source：负责接收数据到Flume Agent的组件
    - 可以处理各种类型、各种格式的日志数据，包括**avro**、thrift、**exec**、jms、**spooling directory、netcat**、sequence generator、syslog、http、legacy
- Channel。Flume自带两种Channel：Memory Channel和File Channel
    - Memory Channel是内存中的队列。Memory Channel在不需要关心数据丢失的情景下适用
    - File Channel将所有事件写到磁盘
- Sink
    - Sink不断地轮询Channel中的事件且批量地移除它们，并将这些事件批量写入到存储或索引系统、或者被发送到另一个Flume Agent
    - Sink组件目的地包括**hdfs、logger、avro**、thrift、ipc、**file、HBase**、solr、自定义
- Events
    - Event是Flume数据传输的基本单元
    - 以事件的形式将数据从源头传输到最终目的地
    - Event由Header和Body两部分组成。
        - Header用来存放该event的一些属性，为K-V结构
        - Body用来存放该条数据，形式为字节数组
- Interceptors 拦截器
    - 允许使用拦截器对传输中的event进行拦截和处理
    - Flume同时支持拦截器链，即由多个拦截器组合而成
- Channel Selectors 选择器，有replicating（默认）和multiplexing两种类型
    - replicating负责将event复制到多个channel
    - multiplexing则根据event的属性和配置的参数进行匹配，匹配成功则发送到指定的channel
- Sink Processors
    - 用户可以将多个sink组成一个整体（sink组）
    - Sink Processors可用于提供组内的所有sink的负载平衡功能，或在时间故障的情况下实现从一个sink到另一个sink的故障转移

常用：
```
Usage: bin/flume-ng <command> [options]...

global options:
  --conf,-c <conf>          use configs in <conf> directory # flume自身的相关配置
  -Dproperty=value          sets a Java system property value
  
agent options:
  --name,-n <name>          the name of this agent (required)
  --conf-file,-f <file>     specify a config file (required if -z missing) # agent的配置
  --zkConnString,-z <str>   specify the ZooKeeper connection to use (required if -f missing)
  --zkBasePath,-p <path>    specify the base path in ZooKeeper for agent configs
```

### 2.常用Source
#### 2.1 Avro Source
Avro Source通过监听Avro端口接收外部Avro客户端流事件（event）
> 支持Avro协议，接收RPC事件请求

关键参数：
- type：类型名称avro。
- bind：绑定的IP。
- port：监听的端口。
- threads:（重要）接收请求的线程数。
> 当需要接收多个avro客户端的数据流时要设置合适的线程数，否则会造成avro客户端数据流积压

#### 2.2 Kafka Source




### 3. Flume进阶
#### 3.1 Flume事务
![image](images/Flume事务.png)

#### 3.2 Flume Agent内部原理
![image](images/FlumeAgent内部原理.png)

ChannelSelector：作用就是选出Event将要被发往哪个Channel
- Replicating（复制）：将同一个Event发往所有的Channel
- Multiplexing（多路复用）：根据相应的原则，将不同的Event发往不同的Channel

SinkProcessor共有三种类型，分别是：
- DefaultSinkProcessor：对应的是单个的Sink
- LoadBalancingSinkProcessor：对应的是Sink Group，实现负载均衡功能
- FailoverSinkProcessor：对应的是Sink Group，实现故障转移功能

