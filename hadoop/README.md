## hadoop

主要参考《hadoop权威指南》第4版，源码地址：[https://github.com/tomwhite/hadoop-book](https://github.com/tomwhite/hadoop-book)

#### windows下运行MR程序
- 单机模式 
    - hadoop单机模式，配置了HADOOP_HOME以后，不需要做任何配置
    - 寻找对应版本的hadoop.dll 和 winutils.exe.各版本下载地址：[https://github.com/steveloughran/winutils](https://github.com/steveloughran/winutils)
    - 把hadoop.dll放到 C:\Windows\System32 下面， winutils.exe 放在$HADOOP_HOME/bin下面
    - idea向运行java程序一样运行、debug
> windows本地运行mr程序时(不提交到yarn,运行在jvm靠线程执行)，hadoop.dll防止报nativeio异常、winutils.exe没有的话报空指针异常。

- 伪分布式
参考资料：
1. [Win7 64位系统上Hadoop单机模式的安装及开发环境搭建 - 黎明踏浪号 - 博客园](https://www.cnblogs.com/benfly/p/8301588.html)
2. [Eclipse连接Hadoop分析的三种方式](https://my.oschina.net/OutOfMemory/blog/776772)
- 建议：在window本地运行单机模式，用于开发调试。在一个虚拟机上部署一个伪分布式，用于测试。再部署一个完全分布式的集群用于模拟生产环境