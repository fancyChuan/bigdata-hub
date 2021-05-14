## HBase环境搭建

**安装前置条件：安装好hadoop以及zookeeper，并正常启动**

> 使用1.3.1版本，1.0版本以后，web端口从60010改为16010

基本配置：[hbase](https://github.com/fancyChuan/bigdata-learn/tree/master/环境搭建/hbase)

启动步骤
```
# 启动所有组件
bin/start-hbase.sh
# 进入shell咯
bin/hbase shell
```

完全分布式部署
- hadoop101为主HMaster， hadoop103为备用的HMaster，所以需要自己新建一个文件backup-masters并写入hadoop103 注意启动的时候在hadoop101
- 完全分布式需要找到HDFS，如果用了HA，那么hbase-site.xml中的hbase.rootdir需要配置成HA的
- 要找到HDFS，有三种方式：
    - 把hadoop的配置目录加到hbase-env.sh的 HBASE_CLASSPATH中 如 export HBASE_CLASSPATH=/usr/local/hadoop/etc/hadoop
    - 把hadoop的hdfs-site.xml拷贝到 $HBASE_HOME/conf下面，也可以采用软链接的方式
    - 把HDFS的配置加到 hbase-site.xml 中
    



===========================================================

```
sudo ln -s /opt/app/hbase-1.3.1 /usr/local/hbase

ln -s /usr/local/hadoop/etc/hadoop/core-site.xml /usr/local/hbase/conf/core-site.xml
ln -s /usr/local/hadoop/etc/hadoop/hdfs-site.xml /usr/local/hbase/conf/hdfs-site.xml
```

