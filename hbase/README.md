## HBase

基本概念：
- Master服务器：一般一个集群只有一个，服务维护表结构信息
    - 只负责各种协调工作（很像打杂），比如建表、删表、移动Region、合并等操作。这些操作的共性是都需要跨RegionServer
- RegionServer服务器：可以有多个，负责存储数据的地方，保存的表数据直接存在HDFS上（调用了HDFS的客户端接口）
    - 非常依赖zookeeper，zk管理了所有RegionServer的信息，包括具体的数据段存放在哪个RegionServer上
    - 客户端每次与HBase连接，都是先跟zk通信，然后再与相应的RegionServer通信
> HBase很特殊，客户端是直连RegionServer的，也就是master挂了以后还能查询，也可以存储和删除，但是无法新建表
- Region：一段数据的集合，一个HBase的表一般拥有一个到多个Region
    - 不能跨服务器，一个服务器可以有一个或多个Region
    - 数据量大的时候，HBase会拆分Region
    - 进行均衡负载的时候，也可能会把Region从一台服务器转到另一台服务器上

- 存储架构
    - HBase中，行与行的列可以完全不一样，不像关系型数据那也有严格规范的表结构