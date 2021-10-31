## FlinkCDC 
github地址：[flink-cdc-connectors](https://github.com/ververica/flink-cdc-connectors)

CDC是Change Data Capture（变更数据获取）的简称。
核心思想是，监测并捕获数据库 的变动（包括数据或数据表的插入 、 更新 以及 删除等），将这些变更按发生的顺序完整记录 下来，
写入到消息中间件中以供其他服务进行订阅及消费。

CDC的两种类型：
- 基于查询
- 基于binlog

![image](img/CDC的两种方式对比.png)

flink-cdc-connectors 组件，这是一个可以直接从mysql、postgresql等数据库直接读取**全量数据**和**增量变更数据**的source组件

#### 支持的版本
| Database | Version |
| --- | --- |
| MySQL | Database: 5.7, 8.0.x <br/>JDBC Driver: 8.0.16 |
| PostgreSQL | Database: 9.6, 10, 11, 12 <br/>JDBC Driver: 42.2.12|
| MongoDB | Database: 4.0, 4.2, 5.0 <br/> MongoDB Driver: 4.3.1 |
| Oracle | Database: 11, 12, 19 <br/>Oracle Driver: 19.3.0.0|

注意mysql5.6.x版本是不支持CDC的


#### 使用
```
public enum StartupMode {
    INITIAL,            // 会先把表的数据用查询的方式查出来（做一个快照），然后再从binlog读取
    EARLIEST_OFFSET,    // 这种会从表最开始的位置开始读取，如果要读到完整数据，那么要求在表创建之前就已经开启binlog
    LATEST_OFFSET,      // 从最新的binlog位置读取
    SPECIFIC_OFFSETS,   // 指定offset
    TIMESTAMP;          // 指定时间戳

    private StartupMode() {
    }
}
```

