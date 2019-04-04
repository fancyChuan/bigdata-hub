## ZooKeeper

Zookeeper是一个具有高可用性的高性能协调服务

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
    - zookeeper不支持递归的删除操作，在删除父节点之前需要先删除子节点
    - delete需要提供两个参数：节点路径+版本号。一致才会删除，版本号设为-1的时候，会直接删除


#### ZooKeeper服务
从模型、操作、实现来了解Zookeeper提供的高性能协调服务
- 数据模型
    - Zookeeper维护这一个树形层次结构，树节点成为znode，并且有一个相关联的ACL，一个znode能存储的数据被限制在1MB以内
    - 数据访问具有原子性：要么全部读到，要么失败什么都读不到。写操作也一样。并且不支持添加操作。
    - 通过路径被引用。路径由Unicode字符串组成。zookeeper是系统保留关键词，不能作为路径。/zookeeper保存管理信息
    - 适合用于构建分布式引用的性质
        - 短暂znode：与客户端关联。客户端退出，短暂znode删除，表示应用退出
        - 顺序号：
        - 观察：观察机制，znode有状态有变更时通知观察者
- 操作
    ```
    create
    delete
    exists(判断是否存在同时查询元数据)
    getACL,setACL
    getChildren
    getData,setData
    sync(客户端znode视图与ZooKeeper同步)
    ```
    - 集合更新：多个基本操作集合成一个操作单元，确保要么同时成功执行，要么同时失败
    - 关于API
        - 执行exists()的时候有同步和异步两种情况。同步直接返回一个封装了元数据的Stat对象，异步则是void只是把操作通过参数传入
        > 同步：public Stat exists(String path, Watcher watcher)
        > 异步：public void exists(String path, Watcher watcher, StatCallable sc, Object ctx)
        - 异步在某些情况下可以提供更好的吞吐量。比如读取一大批znode并处理的时候，采用同步，每一个读操作都是堵塞
    - 观察触发器
- 实现

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