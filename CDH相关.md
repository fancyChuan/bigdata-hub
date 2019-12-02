
- Cloudera Manager的组件
    - Server：负责软件安装、配置，启动和停止服务，管理服务运行的群集。
    - Agent：安装在每台主机上。负责启动和停止的过程，配置，监控主机。
    - Management Service：由一组执行各种监控，警报和报告功能角色的服务。
    - Database：存储配置和监视信息。
    - Cloudera Repository：软件由Cloudera 管理分布存储库。（有点类似Maven的中心仓库）
    - Clients：是用于与服务器进行交互的接口（API和Admin Console）

- cdh启动组件原理
```
# 比如启动namenode
python2.6 /usr/local/appserver/cm-5.10.2/lib64/cmf/agent/build/env/bin/cmf-redactor /usr/local/appserver/cm-5.10.2/lib64/cmf/service/hive/hive.sh hiveserver2
```
主要是用Python执行cmf-redactor，其代码如下
```
#!/usr/bin/env python2.6

import os; activate_this=os.path.join(os.path.dirname(os.path.realpath(__file__)), 'activate_this.py'); 
execfile(activate_this, dict(__file__=activate_this)); del os, activate_this
# 上面两步应该是激活python环境的
# EASY-INSTALL-ENTRY-SCRIPT: 'cmf==5.10.2','console_scripts','cmf-redactor'
__requires__ = 'cmf==5.10.2'
import sys
from pkg_resources import load_entry_point

sys.exit(
   load_entry_point('cmf==5.10.2', 'console_scripts', 'cmf-redactor')()
)
```
- CDH安装
```
# 1. 安装依赖
yum -y install chkconfig python bind-utilspsmisclibxsltzlibsqlitecyrus-sasl-plain cyrus-sasl-gssapi fuse fuse-libs redhat-lsbhttpdmod_ssl
# 2. 创建数据库 
# 集群监控数据库
create database amon DEFAULT CHARSET utf8 COLLATE utf8_general_ci; 
create database hive DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
create database oozie DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
create database hue DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
# 3. 上传离线包并解压
tar -zxvf /opt/software/cloudera-manager-el6-cm5.12.1_x86_64.tar.gz -C /opt/module/cloudera-manager/
# 3. 创建用户 --home指定用户登入的主目录，系统默认为/home/<用户名> --no-create-home不创建主目录
useradd --system --home=/opt/module/cloudera-manager/cm-5.12.1/run/cloudera-scm-server --no-create-home --shell=/bin/false --comment "Cloudera SCM User" cloudera-scm
# 4. 
```
- CDH新建集群后的配置
    - DataNode NameNode的目录、HDFS检查点目录、nodemanager本地目录
    - hive仓库目录 /user/hive/warehouse，元数据服务hive metastore端口 9083
    - 警报：邮件服务主机名、用户名、邮件服务器密码、收件人、自定义警报脚本
    - host monitor存储目录firehose.storage.base.directory=/var/lib/cloudera-host-monitor
    - server monitor存储目录/var/lib/cloudera-service-monitor=/var/lib/cloudera-service-monitor
    - shareLib根目录 oozie.service.WorkflowAppService.system.libpath=/user/oozie
    - oozie服务器数据目录/var/lib/oozie/data
    - zookeeper数据目录/var/lib/zookeeper 事务日志目录/var/lib/zookeeper
    
- 相关目录 
```
/var/log/cloudera-scm-installer : 安装日志目录。
/var/log/* : 相关日志文件（相关服务的及CM的）。
/usr/share/cmf/ : 程序安装目录。
/usr/lib64/cmf/ : Agent程序代码。
/var/lib/cloudera-scm-server-db/data : 内嵌数据库目录。
/usr/bin/postgres : 内嵌数据库程序。
/etc/cloudera-scm-agent/ : agent的配置目录。
/etc/cloudera-scm-server/ : server的配置目录。
/opt/cloudera/parcels/ : Hadoop相关服务安装目录。
/opt/cloudera/parcel-repo/ : 下载的服务软件包数据，数据格式为parcels。
/opt/cloudera/parcel-cache/ : 下载的服务软件包缓存数据。
/etc/hadoop/* : 客户端配置文件目录。
```

- CM 客户端/服务端、Hadoop各个组件的配置文件都在/etc目录下
```
   HDFS Active NameNode数据目录dfs.name.dir: /dfs/nn  
   Standby NameNode数据目录dfs.name.dir: /dfs/nn  
   Secondary NameNode HDFS检查点目录fs.checkpoint.dir:  /dfs/nn  
   日志目录hadoop.log.dir: /var/log/hadoop-hdfs  
   MapReduce JobTracker本地数据目录mapred.local.dir:     /mapred/jt  
   TaskTracker本地数据目录列表mapred.local.dir:    /mapred/local  
   日志目录hadoop.log.dir: /var/log/hadoop-0.20-mapreduce  
   Hive仓库目录hive.metastore.warehouse.dir:      /user/hive/warehouse  
   HiveServer2日志目录: /var/log/hive  
   Zookeeper数据目录dataDir: /var/lib/zookeeper  
   事务日志目录dataLogDir: /var/lib/zookeeper
```


【常用网址】
1. [cdh各组件版本信息](https://www.cloudera.com/documentation/enterprise/release-notes/topics/cdh_vd_cdh_package_tarball_516.html)
2. [cdh各组件maven信息](https://www.cloudera.com/documentation/enterprise/release-notes/topics/cdh_vd_cdh5_maven_repo.html)