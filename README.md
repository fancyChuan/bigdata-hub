## bigdata-hub
大数据相关的技术可以分为：传输、存储、计算三大方面


### 知识体系
- （核心）基础框架
  - [hadoop](./hadoop)：HDFS、MR、YARN及高级话题、企业优化
  - [hive](./hive)：基础使用、优化、SQL编译过程、数据血缘、进阶、权限控制
    - [hive优化](./hive/hive调优)
    - [hivesql功力提升专项](./hive/HiveSQL功力提升.md)：各类难度较高的需求场景的实现方式
    - [hive数据血缘](./hive/hive数据血缘)
    - [数仓工作与建设](./hive/数仓工作与建设)
    - [数仓建模](./hive/数仓建模)
  - [spark](./spark)：spark-core、spark-sql、streaming、优化、进阶
  - [flink](./flink)：flink基础知识、API、CEP、CDC、流式架构、案例实践
- 数据存储
  - [hbase](./hbase)：核心API、进阶、与Hive和MR集成、Phoenix集成
  - [kafka](./kafka)：命令行、API、企业级实战
  - [elasticsearch](./elasticsearch)
  - [zookeeper](./zookeeper)
- 数据传输（数据交换、数据集成）
  - [datax](./datax)
  - [sqoop](./sqoop)
  - [flume](./flume)
  - [seatunnel](./seatunnel)
  - [bitsail](./bitsail)：字节开源的高性能数据集成引擎
- 大数据调度：满足复杂大规模作业的调度场景
  - [oozie](bigdata-scheduler/oozie)
  - [azkaban](bigdata-scheduler/azkaban)
  - [dolphinscheduler](bigdata-scheduler/dolphinscheduler)
- 第三方or商业集成技术
  - [aliyun-bigdata](./aliyun-bigdata)：阿里云大数据（包括DataWorks、MaxCompute等）
  - [dss](./dss)：微众开源一站式数据平台（DataSphere Studio）
    - 计算中间件：linkis
    - 数据交换：exchangis

- 环境搭建
  - [apache版本搭建](./环境搭建)
  - [CDH集群搭建](./环境搭建/CM+CDH安装)
  - [HDP集群搭建](./环境搭建/Ambari+HDP安装)
- 大数据建设
  - [可视化](./大数据建设/可视化BI)
  - [标签/指标体系](./大数据建设/指标&标签体系建设)
  - [数据质量](./大数据建设/数据质量管理)
  - [混合计算](./大数据建设/混合计算)
- 前沿
  - 增强性数据分析
  - [数据中台](./大数据建设/数据中台建设)
  - [数据湖](./大数据建设/数据湖)
    - [Hudi](./hudi) 
  - [湖仓一体Lakehouse](./大数据建设/湖仓Lakehouse)
  - [DataOps](./大数据建设/DataOps)
- 其他
  - [数字化转型](./数字化转型)

### 程序包下载
- Apache版本： [http://archive.apache.org/dist/](http://archive.apache.org/dist/)
- CDH5单个版本： [http://archive.cloudera.com/cdh5/cdh/5/](http://archive.cloudera.com/cdh5/cdh/5/)
- CDH5离线包： [https://archive.cloudera.com/cdh5/parcels/](https://archive.cloudera.com/cdh5/parcels/)
- CDH6： https://archive.cloudera.com/cdh6/
- CM6：  https://archive.cloudera.com/cm6/


- 清华Apache镜像： [https://mirrors.tuna.tsinghua.edu.cn/apache/](https://mirrors.tuna.tsinghua.edu.cn/apache/)


### 学习资料
- [《离线和实时大数据开发实战》-朱松岭](https://weread.qq.com/web/reader/7e332cb05e45157e3d0ec59kc81322c012c81e728d9d180)
- [《Hadoop构建数据仓库实践》-王雪迎](https://weread.qq.com/web/reader/1d532310719b20661d52380)
- [《Hadoop技术内幕：深入解析YARN架构设计与实现原理》](https://weread.qq.com/web/reader/71a32ab0597cf871a51c384kc81322c012c81e728d9d180)
- 《spark快速大数据分析(learning-spark)》: [https://github.com/databricks/learning-spark](https://github.com/databricks/learning-spark)
- [《Hive性能调优实战》-林志煌](https://weread.qq.com/web/reader/a503221071a486c0a503e7akc81322c012c81e728d9d180)
  - 笔记：[01感受hive性能调优的多样式](hive/hive调优/01感受hive性能调优的多样式.md)
  - 笔记：[02Hive问题排查与调优思路](hive/hive调优/02Hive问题排查与调优思路.md)
- 《数据仓库》
  - 笔记：[《数据仓库-Inmon第4版》](hive/数据仓库/数据仓库第4版/《数据仓库-Inmon第4版》.md)
