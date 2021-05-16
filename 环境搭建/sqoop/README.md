## Sqoop安装

#### 1 下载并解压
1) 下载地址：http://mirrors.hust.edu.cn/apache/sqoop/1.4.6/
2) 上传安装包sqoop-1.4.6.bin__hadoop-2.0.4-alpha.tar.gz到虚拟机中
3) 解压sqoop安装包到指定目录，如：
```$ tar -zxf sqoop-1.4.6.bin__hadoop-2.0.4-alpha.tar.gz -C /opt/app/```
4) 配置软链接到/usr/local
```xcall sudo ln -s /opt/app/sqoop-1.4.6.bin__hadoop-2.0.4-alpha/ /usr/local/sqoop```
#### 2 修改配置文件
Sqoop的配置文件与大多数大数据框架类似，在sqoop根目录下的conf目录中。
1) 重命名配置文件
$ mv sqoop-env-template.sh sqoop-env.sh
2) 修改配置文件
```
vim sqoop-env.sh
export HADOOP_COMMON_HOME=/usr/local/hadoop
export HADOOP_MAPRED_HOME=/usr/local/hadoop
export HIVE_HOME=/usr/local/hive
export ZOOKEEPER_HOME=/usr/local/zookeeper
export ZOOCFGDIR=/usr/local/zookeeper
export HBASE_HOME=/usr/local/hbase
```
#### 3 拷贝JDBC驱动
> 可以从hive的安装目录中拷贝jdbc驱动到sqoop的lib目录下，如：
```
cp  /opt/app/hive-1.2.1-bin/lib/mysql-connector-java-5.1.47.jar /opt/app/sqoop-1.4.6.bin__hadoop-2.0.4-alpha/lib/
```

#### 4 测试是否安装成功
```
bin/sqoop list-databases --connect jdbc:mysql://hphost:3306/ --username root --password 123456 
```

#### 5. 分发安装包
```
xsync /opt/app/sqoop-1.4.6.bin__hadoop-2.0.4-alpha
```