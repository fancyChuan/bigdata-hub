## Flink基础知识与API

Flink的重要特点；
- 事件驱动型（event-driven）:根据到来的事件触发计算、状态更新或其他外部动作，比如Kafka
- 流与批的世界观
    - Flink世界观中一切都是由流组成的，离线数据是有界限的流，实时数据是一个没有界限的流【有界流、无界流】
    - 而在Spark中，一切都是批次组成的，离线数据是大批次，实时数据是由一个一个无限的小批次组成的
    - 无界数据流：数据流有一个开始但是灭有结束
    - 有界数据流：数据流有明确定义的开始和结束
- 分层API
    - 高级语言：SQL
    - 声明式DSL语法：Table API
    - 核心API：DataStream/DataSet API 
    - 低级API：Stateful Stream Processing
- 支持有状态计算：flink1.4以后也支持中间结果缓存，相当于checkpoint操作
- 支持exactly-once语义：有且仅执行一次
- 支持事件时间（EventTime）

流处理API：environment -> source -> transform -> sink

### 1. Environment
- getExecutionEnvironment
    - 创建一个执行环境，表示当前执行程序的上下文
        - 程序独立调用，此方法返回本地执行环境
        - 程序提交到集群，此方法返回集群的执行环境
```
val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
val env = StreamExecutionEnvironment.getExecutionEnvironment
```
- createLocalEnvironment：返回本地执行环境，需要在调用时指定默认的并行度
- createRemoteEnvironment
```
val env = StreamExecutionEnvironment.createLocalEnvironment(1)
val env = ExecutionEnvironment.createRemoteEnvironment("jobmanage-hostname", 6123,"YOURPATH//wordcount.jar")
```

### 2. DataSource
- 基于文本：readTextFile
- 基于Socket：socketTextStream
- 基于集合：fromCollection
- 自定义输入：addSource
    - FlinkKafkaConsumer011 从kafka队列中消费

代码参见：[SourceApp.scala](src/main/scala/cn/fancychuan/scala/SourceApp.scala)