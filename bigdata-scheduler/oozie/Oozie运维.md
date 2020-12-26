## Oozie运维

#### 1.时区统一
- 修改hue时区为Asia/Shanghai
```
# cm上搜索time_zone
# 改为 Asia/Shanghai
```
- 修改Oozie时区
```
# 1.登录Cloudera Manager 进入Ooize服务的配置界面搜索“oozie-site.xml”
# 2. 增加配置
<property>
    <name>oozie.processing.timezone</name>
    <value>GMT+0800</value>
</property>
```
- 重启重新部署


#### 2. 其他修改项
- 任务查找频率，默认5分钟
```
# 在cm进入oozie服务配置界面搜索“lookup.interval”
将 oozie.service.CoordMaterializeTriggerService.lookup.interval 设置为2分钟
```


- oozie Server运行一段时间就奔溃退出
```
# oozie server的完整启动参数
/usr/local/jdk/bin/java \ 
-Djava.util.logging.config.file=/var/lib/oozie/tomcat-deployment/conf/logging.properties \ 
-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager \ 
-Djdk.tls.ephemeralDHKeySize=2048 \ 
-Xms52428800 \ 
-Xmx52428800 \ 
-XX:+HeapDumpOnOutOfMemoryError \ 
-XX:HeapDumpPath=/tmp/oozie_oozie-OOZIE_SERVER-dfea509e296b6345894e6dd984a10d07_pid1810.hprof \ 
-XX:OnOutOfMemoryError=/opt/cm-5.13.3/lib64/cmf/service/common/killparent.sh \ 
-Doozie.home.dir=/opt/cloudera/parcels/CDH-5.13.3-1.cdh5.13.3.p0.2/lib/oozie \ 
-Doozie.config.dir=/opt/cm-5.13.3/run/cloudera-scm-agent/process/670-oozie-OOZIE_SERVER \ 
-Doozie.log.dir=/var/log/oozie \ 
-Doozie.log.file=oozie-cmf-oozie-OOZIE_SERVER-dxbigdata101.log.out \ 
-Doozie.config.file=oozie-site.xml \ 
-Doozie.log4j.file=log4j.properties \ 
-Doozie.log4j.reload=10 \ 
-Doozie.http.hostname=dxbigdata101 \ 
-Doozie.http.port=11000 \ 
-Djava.net.preferIPv4Stack=true \ 
-Doozie.admin.port=11001 \ 
-Dderby.stream.error.file=/var/log/oozie/derby.log \ 
-Doozie.instance.id=dxbigdata101 \ 
-Djava.library.path=/opt/cloudera/parcels/CDH-5.13.3-1.cdh5.13.3.p0.2/lib/hadoop/lib/native \ 
-Doozie.https.ciphers=TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384,TLS_ECDH_RSA_WITH_AES_256_CBC_SHA,TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256,TLS_ECDH_RSA_WITH_AES_128_CBC_SHA,TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA,TLS_RSA_WITH_AES_256_CBC_SHA256,TLS_RSA_WITH_AES_256_CBC_SHA,TLS_RSA_WITH_AES_128_CBC_SHA256,TLS_RSA_WITH_AES_128_CBC_SHA,TLS_RSA_WITH_3DES_EDE_CBC_SHA \ 
-Djava.endorsed.dirs=/opt/cloudera/parcels/CDH-5.13.3-1.cdh5.13.3.p0.2/lib/bigtop-tomcat/endorsed \ 
-classpath \ 
/opt/cloudera/parcels/CDH-5.13.3-1.cdh5.13.3.p0.2/lib/bigtop-tomcat/bin/bootstrap.jar \ 
-Dcatalina.base=/var/lib/oozie/tomcat-deployment \ 
-Dcatalina.home=/opt/cloudera/parcels/CDH-5.13.3-1.cdh5.13.3.p0.2/lib/bigtop-tomcat \ 
-Djava.io.tmpdir=/opt/cm-5.13.3/run/cloudera-scm-agent/process/670-oozie-OOZIE_SERVER/temp \ 
org.apache.catalina.startup.Bootstrap \ 
start
```
> 可以发现Oozie Server的堆栈大小为50M，在/tmp下可以发现，堆栈不够导致内存溢出
```
[appuser@dxbigdata101 tmp]$ ll
drwxr-xr-x 4 hdfs      hdfs            32 Dec  1 14:40 Jetty_localhost_43433_datanode____.n4ry78
-rw-r--r-- 1 hive      hive         56782 Dec 10 11:05 libnetty-transport-native-epoll4703897735695368087.so
-rw------- 1 oozie     oozie     67060240 Dec  4 14:16 oozie_oozie-OOZIE_SERVER-dfea509e296b6345894e6dd984a10d07_pid13701.hprof
-rw------- 1 oozie     oozie     66668926 Dec  5 21:50 oozie_oozie-OOZIE_SERVER-dfea509e296b6345894e6dd984a10d07_pid14480.hprof
-rw------- 1 oozie     oozie     66432559 Dec  6 19:20 oozie_oozie-OOZIE_SERVER-dfea509e296b6345894e6dd984a10d07_pid15545.hprof
-rw------- 1 oozie     oozie     66304838 Dec  8 23:40 oozie_oozie-OOZIE_SERVER-dfea509e296b6345894e6dd984a10d07_pid28270.hprof
-rw------- 1 oozie     oozie     67789502 Dec  4 02:44 oozie_oozie-OOZIE_SERVER-dfea509e296b6345894e6dd984a10d07_pid2861.hprof
-rw------- 1 oozie     oozie     66859571 Dec  7 10:33 oozie_oozie-OOZIE_SERVER-dfea509e296b6345894e6dd984a10d07_pid3950.hprof
```
> 解决方法：增加Oozie Server的堆栈（Cloudera Manager上进入Oozie之后，点击“配置”选项卡，搜索“heap”）
