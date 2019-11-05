## HadoopHA下的ZooKeeper存储详情

- ActiveBreadCrumb： 存活面包屑。作用是，加入这个NameNode死掉了，能够留下一点线索，方便后续启动NameNode的时候决定如何操作
- ActiveStandbyElectorLock
```
[zk: localhost:2181(CONNECTED) 25] get /hadoop-ha/ns1/ActiveBreadCrumb       

ns1 nn1 s01 �>(�>
cZxid = 0x200000008
ctime = Sun Apr 21 01:04:43 CST 2019
mZxid = 0x2d00000025
mtime = Sat Nov 02 01:23:49 CST 2019
pZxid = 0x200000008
cversion = 0
dataVersion = 64
aclVersion = 0
ephemeralOwner = 0x0      # 这是一个永久节点
dataLength = 21
numChildren = 0
[zk: localhost:2181(CONNECTED) 26] get /hadoop-ha/ns1/ActiveStandbyElectorLock

ns1 nn1 s01 �>(�>
cZxid = 0x2d00000024
ctime = Sat Nov 02 01:23:49 CST 2019
mZxid = 0x2d00000024
mtime = Sat Nov 02 01:23:49 CST 2019
pZxid = 0x2d00000024
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x36e1da7e8a10003  # 这是一个临时节点
dataLength = 21
numChildren = 0
```