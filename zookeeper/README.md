## ZooKeeper

分布式应用的主要困难是会出现“部分错误”(partial failure)，这是分布式系统固有的特征。ZooKeeper可以提供一组工具，能够对部分失败进行正确的处理

> 部分失败： 一条消息在两个节点之间传送，发送网络错误，发送者无法知道接受者是否收到这条消息

独立模式运行zk的最低要求：
```
tickTime=2000
dataDir=/opt/modules/zookeeper-3.4.5/data/zkData
clientPort=2181
```

将ZooKeeper视为一个具有高可用性的文件系统，只不过没有文件和目录，而是统一使用节点node的概念，称为znode，同时具有文件和目录的功能

#### java API
- 创建组
- 加入组
    - 每个组成员作为一个程序运行
    - 当程序退出时，组成员应当从组中删除
    - 通过在ZooKeeper的命名空间中使用短暂znode来代表一个组成员
- 列出组成员
- 删除组


#### ZooKeeper服务


---
ZooKeeper角色
- leader领导者，负责进行投票的发起和决议，更新系统状态
- learner
    - follower 跟随者，用于接受客户请求并向客户端返回结果，在选主过程中参与投票
    - observer 可以接受客户端连接，将写请求转发给leader节点，但不参与投票，只同步leader的状态。目的是为了扩展系统，提高读取速度
- client 请求发起方

ZooKeeper典型应用场景
- 统一命名服务 Name Service
- 配置管理
- 集群管理
- 共享锁/同步锁
> ZooKeeper从设计模型的角度讲，是一个基于观察者模式设计的分布式服务管理框架，负责存储和管理大家都关心的数据，然后接受观察者的注册。
> 一旦这些数据的状态发生变化，ZooKeeper就负责通知已经注册的观察者，从而实现类似于Master/Slave管理模式

相关命令
```
# 启动
bin/zkServer.sh start
# 查看状态
bin/zkServer.sh status
# 进入客户端
bin/zkCli.sh 
bin/zkCli.sh -server s00:2181 
```
客户端支持的命令
```
ZooKeeper -server host:port cmd args
        connect host:port
        get path [watch]
        ls path [watch]
        set path data [version]
        rmr path
        delquota [-n|-b] path
        quit 
        printwatches on|off
        create [-s] [-e] path data acl
        stat path [watch]
        close 
        ls2 path [watch]
        history 
        listquota path
        setAcl path acl
        getAcl path
        sync path
        redo cmdno
        addauth scheme auth
        delete path [version]
        setquota -n|-b val path
```