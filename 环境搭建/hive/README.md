## Hive搭建

#### 1. mysql安装


#### 2. Hive配置
解压：tar -zxvf apache-hive-1.2.1-bin.tar.gz

- hive-env.sh
主要配置HADOOP_HOME和HIVE_CONF_DIR这两
> mv hive-env.sh.template hive-env.sh
```
export HADOOP_HOME=/opt/module/hadoop-2.7.2
export HIVE_CONF_DIR=/opt/module/hive/conf
```

- hive-site.xml
```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
	<property>
	  <name>javax.jdo.option.ConnectionURL</name>
	  <value>jdbc:mysql://hadoop102:3306/metastore?createDatabaseIfNotExist=true</value>
	  <!--使用下面这个配置设置了编码，但还是需要数据库的编码类型为latin1，否则还是会无法建表-->
	  <!--<value>jdbc:mysql://s03:3306/metastore?createDatabaseIfNotExist=true&amp;useUnicode=true&amp;characterEncoding=UTF-8</value>-->
	  <description>JDBC connect string for a JDBC metastore</description>
	</property>

	<property>
	  <name>javax.jdo.option.ConnectionDriverName</name>
	  <value>com.mysql.jdbc.Driver</value>
	  <description>Driver class name for a JDBC metastore</description>
	</property>

	<property>
	  <name>javax.jdo.option.ConnectionUserName</name>
	  <value>root</value>
	  <description>username to use against metastore database</description>
	</property>

	<property>
	  <name>javax.jdo.option.ConnectionPassword</name>
	  <value>123456</value>
	  <description>password to use against metastore database</description>
	</property>
</configuration>
```
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