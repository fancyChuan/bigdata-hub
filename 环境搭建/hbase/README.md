

### 4. Hbase
使用1.1.2版本，1.0版本以后，web端口从60010改为16010

基本配置：[hbase](https://github.com/fancyChuan/bigdata-learn/tree/master/环境搭建/hbase)

启动步骤
```
# 启动所有组件
bin/start-hbase.sh
# 进入shell咯
bin/hbase shell
```

完全分布式部署
- s01为主HMaster， s03为备用的HMaster，所以需要自己新建一个文件backup-masters并写入s03 注意启动的时候在s01
- 完全分布式需要找到HDFS，如果用了HA，那么hbase-site.xml中的hbase.rootdir需要配置成HA的
- 要找到HDFS，有三种方式：
    - 把hadoop的配置目录加到hbase-env.sh的 HBASE_CLASSPATH中 如 export HBASE_CLASSPATH=/usr/local/hadoop/etc/hadoop
    - 把hadoop的hdfs-site.xml拷贝到 $HBASE_HOME/conf下面
    - 把HDFS的配置加到 hbase-site.xml 中
    
> 看到1.1.2版本lib下的hadoop相关jar包是2.5.1的，而集群用的是2.7.1，是否会有问题？？