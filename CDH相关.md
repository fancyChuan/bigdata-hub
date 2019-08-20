

- 相关目录 
```
/var/log/cloudera-scm-installer : 安装日志目录。
/var/log/* : 相关日志文件（相关服务的及CM的）。
/usr/share/cmf/ : 程序安装目录。
/usr/lib64/cmf/ : Agent程序代码。
/var/lib/cloudera-scm-server-db/data : 内嵌数据库目录。
/usr/bin/postgres : 内嵌数据库程序。
/etc/cloudera-scm-agent/ : agent的配置目录。
/etc/cloudera-scm-server/ : server的配置目录。
/opt/cloudera/parcels/ : Hadoop相关服务安装目录。
/opt/cloudera/parcel-repo/ : 下载的服务软件包数据，数据格式为parcels。
/opt/cloudera/parcel-cache/ : 下载的服务软件包缓存数据。
/etc/hadoop/* : 客户端配置文件目录。
```

- CM 客户端/服务端、Hadoop各个组件的配置文件都在/etc目录下
```
   HDFS Active NameNode数据目录dfs.name.dir: /dfs/nn  
   Standby NameNode数据目录dfs.name.dir: /dfs/nn  
   Secondary NameNode HDFS检查点目录fs.checkpoint.dir:  /dfs/nn  
   日志目录hadoop.log.dir: /var/log/hadoop-hdfs  
   MapReduce JobTracker本地数据目录mapred.local.dir:     /mapred/jt  
   TaskTracker本地数据目录列表mapred.local.dir:    /mapred/local  
   日志目录hadoop.log.dir: /var/log/hadoop-0.20-mapreduce  
   Hive仓库目录hive.metastore.warehouse.dir:      /user/hive/warehouse  
   HiveServer2日志目录: /var/log/hive  
   Zookeeper数据目录dataDir: /var/lib/zookeeper  
   事务日志目录dataLogDir: /var/lib/zookeeper
```


【常用网址】
1. [cdh各组件版本信息](https://www.cloudera.com/documentation/enterprise/release-notes/topics/cdh_vd_cdh_package_tarball_516.html)
2. [cdh各组件maven信息](https://www.cloudera.com/documentation/enterprise/release-notes/topics/cdh_vd_cdh5_maven_repo.html)