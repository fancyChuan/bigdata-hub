## 集成Phoenix


因为集群用的CDH是5.13.3，带的hbase是1.12.0版本的
因此从这里下载Phoenix要选择相应的版本


parcels包下载地址：[http://archive.cloudera.com/cloudera-labs/phoenix/parcels/1.2.0/](http://archive.cloudera.com/cloudera-labs/phoenix/parcels/1.2.0/)

```
CLABS_PHOENIX-4.5.2-1.clabs_phoenix1.2.0.p0.774-el7.parcel	2018-02-20 17:51	241.87MB
CLABS_PHOENIX-4.5.2-1.clabs_phoenix1.2.0.p0.774-el7.parcel.sha1	2018-02-20 17:51	41B
```

**似乎这个版本的不兼容CDH1.13.3**
。。。

TODO：待验证吧。我决定先放弃CDH版本的HBase了，改用自己弄apache版本
```
phoenix官方代码

https://github.com/apache/phoenix/tree/4.14-cdh5.13
貌似可以考虑基于这个版本编译
```

参考资料：

大数据之CDH5.16.1集成Phoenix - 简书
https://www.jianshu.com/p/0b80f1098c8f

(17条消息) CDH 5.13.0 集成 Phoenix_jast-CSDN博客
https://blog.csdn.net/zhangshenghang/article/details/97392236

CDH版Phoenix的安装（图文详解）
https://www.cnblogs.com/zlslch/p/7096402.html