## CM+CDH安装大数据集群

CM也就是 cloudera manager

安装包下载地址：
- CDH5： https://archive.cloudera.com/cdh5/
- CDH6： https://archive.cloudera.com/cdh6/
- CM5：  http://archive.cloudera.com/cm5/cm/5/
- CM6：  https://archive.cloudera.com/cm6/
> CDH官方开始收费后，以上地址已无法下载
> 目前这个地址还可以：http://ro-bucharest-repo.bigstepcloud.com/cloudera-repos/cm5/redhat/7/x86_64/

这里选择5.13.3版本，centos7环境下
```
# 下载cloudera manager
http://archive.cloudera.com/cm5/cm/5/cloudera-manager-centos7-cm5.13.3_x86_64.tar.gz
# 下载CDH离线包
http://archive.cloudera.com/cdh5/parcels/5.13.3/CDH-5.13.3-1.cdh5.13.3.p0.2-el7.parcel
http://archive.cloudera.com/cdh5/parcels/5.13.3/CDH-5.13.3-1.cdh5.13.3.p0.2-el7.parcel.sha1
http://archive.cloudera.com/cdh5/parcels/5.13.3/manifest.json
```

CDH安装（准备三台机器CDH101，CDH102，CDH103）
> 命令使用的xsync和xcall是自定义脚本，参见 [centos机器准备](../centos机器准备.md)
```
# 1. 每台机器上安装依赖
yum -y install chkconfig python bind-utils psmisc libxslt zlib sqlite cyrus-sasl-plain cyrus-sasl-gssapi fuse fuse-libs redhat-lsb httpd mod_ssl
# 2. 创建数据库 
# 集群监控数据库
create database amon DEFAULT CHARSET utf8 COLLATE utf8_general_ci; 
create database hive DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
create database oozie DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
create database hue DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
# 3. 【CDH101上操作】上传离线包，并解压CM安装包
tar -zxvf /opt/software/cloudera-manager-centos7-cm5.13.3_x86_64.tar.gz -C /opt/
# 拷贝CDH离线资源包
cp /opt/software/CDH-5.13.3-1.cdh5.13.3.p0.2-el7.parcel* /opt/cloudera/parcel-repo/
cp /opt/software/manifest.json /opt/cloudera/parcel-repo/
mv /opt/cloudera/parcel-repo/CDH-5.13.3-1.cdh5.13.3.p0.2-el7.parcel.sha1 /opt/cloudera/parcel-repo/CDH-5.13.3-1.cdh5.13.3.p0.2-el7.parcel.sha

# 4. 【以root用户操作】每台机器上创建用户cloudera-scm
useradd --system --home=/opt/cm-5.13.3/run/cloudera-scm-server --no-create-home --shell=/bin/false --comment "Cloudera SCM User" cloudera-scm
##  --home指定用户登入的主目录，系统默认为/home/<用户名> --no-create-home不创建主目录 --shell=/bin/false最严格的非登录用户

# 5. 【以root用户操作】修改cm agent，制定其cm server为cdh101。
vim /opt/cm-5.13.3/etc/cloudera-scm-agent/config.ini 
修改server_host 为  server_host=cdh101
之后分发到其他的两台机器上
xsync /opt/cm-5.13.3
xsync /opt/cloudera

# 6. 将cm-5.13.3和cloudera两个目录授权给cloudera-scm用户
chown -R cloudera-scm:cloudera-scm /opt/cm-5.13.3
chown -R cloudera-scm:cloudera-scm /opt/cloudera 

# 7. 配置cm数据库
- 拷贝mysql连接jar包到 /usr/share/java/，注意需要改名为 mysql-connector-java.jar
cp /opt/software/mysql-connector-java-5.1.47.jar /usr/share/java/
mv /usr/share/java/mysql-connector-java-5.1.47.jar /usr/share/java/mysql-connector-java.jar
- 安装mysql的compact包
- 初始化cm数据库
/opt/cm-5.13.3/share/cmf/schema/scm_prepare_database.sh mysql cm -hhphost -uroot -p123456 --scm-host cdh101 scm scm scm

# 8. 启动cm server
/opt/cm-5.13.3/etc/init.d/cloudera-scm-server start
在三台机器上启动 cm agent
xcall /opt/cm-5.13.3/etc/init.d/cloudera-scm-agent start 
```
>  补充，可以再设置下下面的内容
```
# vim /etc/rc.local
# 开机禁止启用透明大页面压缩，避免可能会导致的重大性能问题
echo never > /sys/kernel/mm/transparent_hugepage/defrag
echo never > /sys/kernel/mm/transparent_hugepage/enabled
```


CDH新建集群后的配置
- DataNode NameNode的目录、HDFS检查点目录、nodemanager本地目录
- hive仓库目录 /user/hive/warehouse，元数据服务hive metastore端口 9083
- 警报：邮件服务主机名、用户名、邮件服务器密码、收件人、自定义警报脚本
- host monitor存储目录firehose.storage.base.directory=/var/lib/cloudera-host-monitor
- server monitor存储目录/var/lib/cloudera-service-monitor=/var/lib/cloudera-service-monitor
- shareLib根目录 oozie.service.WorkflowAppService.system.libpath=/user/oozie
- oozie服务器数据目录/var/lib/oozie/data
- zookeeper数据目录/var/lib/zookeeper 事务日志目录/var/lib/zookeeper


参考资料：
[CDH运维：CDH官方包收费之后](https://blog.csdn.net/adorechen/article/details/116661772)

