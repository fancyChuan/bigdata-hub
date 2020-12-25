## 大数据运维


#### 集群节点角色
集群中的每个节点托管者不同的服务，其所托管的服务类型决定了该节点在集群中的作用。具体可以划分为：
- 主节点；运行主Hadoop服务
- 工作节点：比如DataNode、NodeManager
- 管理节点：运行帮助配置、管理和监控集群的服务，比如AmbariServer、Ganglia、Nagios、Puqqet和Chef之类的服务
- 网关节点：也称为边缘节点，是存储客户端配置文件的地方
    - 数据网关节点：HDFS、HttpFS、NFS网关
    - SQL网关节点：Hive、HiveServer2、WebHCat Server
    - 用户网关节点：客户端配置、Hue服务器、Oozie服务器、Kerberos Ticket Renewer
    
> 网关在Cloudera Manager中显示为Getway，如下图所示，一般我们通过CM来重新部署客户端配置的时候，就是通过该角色来进行了。
> 比如集群新增了一个节点，如果这个节点上用sqoop推送ORC格式的hive表到MySQL表中，那么此时sqoop会查看该节点上hive-site.xml的metastore的配置信息
> 而这个新增的节点并没有添加一个Hive的Getway，那么sqoop将只会读到一个默认的hive-site.xml

![image](images/Hive的Gateway.png)