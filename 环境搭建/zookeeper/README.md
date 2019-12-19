### 2. ZooKeeper
基本配置：[zookeeper](https://github.com/fancyChuan/bigdata-learn/tree/master/环境搭建/zookeeper)

使用说明：注意配置的目录地址以及需要更换主机名，把配置文件放在 $ZOOKEEPER_HOME/conf下面

部署：
```
tar -zxvf /opt/software/zookeeper-3.4.10.tar.gz -C /opt/app/
cd /opt/app/zookeeper-3.4.10/
# 创建软链接
xcall sudo ln -s /opt/app/zookeeper-3.4.10 /usr/local/zookeeper
# 创建zk需要用的目录
xcall mkdir -p /data/zookeeper/zkData
# 修改配置 参考zoo.cfg文件
mv conf/zoo_sample.cfg conf/zoo.cfg 
# 分发zk及配置文件
xsync /opt/app/zookeeper-3.4.10

# 1. 创建myid，每台主机需要有个唯一的id号，且与zoo.cfg中的server.X的X一致
进入dataDir=/data/zookeeper/zkData，然后vim myid 输入数字，然后保存
```

启动步骤
```
在每个节点启动
bin/zkServer.sh start
```