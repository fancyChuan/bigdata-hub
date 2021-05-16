## Hive搭建

使用1.2.1版本，2.0版本以后可能会不支持MR引擎，更多的是使用spark

基本配置：[hive](https://github.com/fancyChuan/bigdata-learn/tree/master/环境搭建/hive)

启动步骤
```
# 启动metastore服务在后台运行
nohup hive --service metastore &
# 启动hiveserver2
nohup hive --service hiveserver2 &
# 启动hive cli或者beeline
```

#### 1. mysql安装
卸载mysql和MariaDB

安装libaio依赖

安装服务端和客户端

#### 2. Hive配置
解压：tar -zxvf apache-hive-1.2.1-bin.tar.gz

- hive-env.sh
主要配置HADOOP_HOME和HIVE_CONF_DIR这两
> mv hive-env.sh.template hive-env.sh
```
export HADOOP_HOME=/usr/local/hadoop
export HIVE_CONF_DIR=/usr/local/hive/conf
```

- hive-site.xml用户配置文件（默认的配置文件是hive-site.xml）
```
    <!-- metastore config -->
    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://hphost:3306/metastore?createDatabaseIfNotExist=true</value>
        <!--使用下面这个配置设置了编码，但还是需要数据库的编码类型为latin1，否则还是会无法建表-->
	    <!--<value>jdbc:mysql://s03:3306/metastore?createDatabaseIfNotExist=true&amp;useUnicode=true&amp;characterEncoding=UTF-8</value>-->
    </property>
    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>root</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>123456</value>
    </property>
    <!-- 不加下面这项配置，那么可以不用启动metastore，启动hiveserver2即可 -->
    <!--    <property>-->
    <!--        <name>hive.metastore.uris</name>-->
    <!--        <value>thrift://hadoop101:9083</value>-->
    <!--        <description>IP address (or fully-qualified domain name) and port of the metastore host</description>-->
    <!--    </property>-->

```
> 重点关注下

这里配置mysql数据库的时候会有个坑。如果通过bin/hive启动hive的时候报如下的错误，那么很大可能就是数据库的编码不一致。我们用mysql很多默认是utf-8，而hive则需要是latin1，虽然感觉很不应该。

```
hive (default)> create table aa (id int, name string);
Moved: 'hdfs://ns1/user/hive/warehouse/aa' to trash at: hdfs://ns1/user/beifeng/.Trash/Current
Moved: 'hdfs://ns1/user/hive/warehouse/aa' to trash at: hdfs://ns1/user/beifeng/.Trash/Current
Moved: 'hdfs://ns1/user/hive/warehouse/aa' to trash at: hdfs://ns1/user/beifeng/.Trash/Current
FAILED: Execution Error, return code 1 from org.apache.hadoop.hive.ql.exec.DDLTask. MetaException(message:For direct MetaStore DB connections, we don't support retries at the client level.)
```

解决方法：
```
mysql> alter database metastore character set latin1;
```

- hive-log4j.properties
```
hive.log.dir=/opt/module/hive/logs
```


