## CDH集群迁移
从bigdata101-bigdata104迁移到dxbigdata101-dxbigdata103

迁移场景：
- 冷热集群数据分类存储
- 集群数据整体搬迁
> 比如 从bigdata101-bigdata104迁移到dxbigdata101-dxbigdata103
- 数据的准实时同步

数据迁移要考虑的要素
- Bandwidth：带宽。大量使用带宽，可能会影响线上的业务，所以需要考虑带宽的限流
- Performance：性能。一般都考虑性能更佳的分布式程序
- Data-Increment：增量同步。判断文件是否有变化
    - 文件大小。如果不一致，说明有变更，变更有两种类型：截取原文件长度的内容，计算checksum（校验和），如果不变，就说明原文件被追加了内容。否则，原始内容被修改了。
    - 文件大小也一致，也计算下checksum（校验和），相同就说明原始文件没有变化。
- Syncable：数据迁移的同步性。定期需要的迁移要在指定时间内完成。



hive需要修改主机名的地方：
- metastore的数据库主机
- hive元数据库的SDS表和DBS表


#### 源码分析distcp

1. [distcp流程分析](https://blog.csdn.net/answer100answer/article/details/102710311)
2. [Hadoop集群间文件拷贝](https://yampery.github.io/2019/01/29/distcp/)



