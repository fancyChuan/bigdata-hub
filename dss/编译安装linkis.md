## 编译安装Linkis


报错：
```
[ERROR] Failed to execute goal on project linkis-hive-engine: Could not resolve dependencies for project com.webank.wedatasphere.linkis:linkis-hive-engine:jar:0.9.4: The following artifacts could not be resolved: org.pentaho:pentaho-aggdesigner-algorithm:jar:5.1.5-jhyde, eigenbase:eigenbase-properties:jar:1.1.4: Failure to find org.pentaho:pentaho-aggdesigner-algorithm:jar:5.1.5-jhyde in https://repository.cloudera.com/artifactory/cloudera-repos/ was cached in the local repository, resolution will not be reattempted until the update interval of cloudera has elapsed or updates are forced -> [Help 1]
```
解决方法：
```
wget https://repo.spring.io/plugins-release/org/pentaho/pentaho-aggdesigner-algorithm/5.1.5-jhyde/pentaho-aggdesigner-algorithm-5.1.5-jhyde.jar

```
错误2
```
[ERROR] Failed to execute goal on project linkis-hive-engine: Could not resolve dependencies for project com.webank.wedatasphere.linkis:linkis-hive-engine:jar:0.9.4: Failure to find eigenbase:eigenbase-properties:jar:1.1.4 in https://repository.cloudera.com/artifactory/cloudera-repos/ was cached in the local repository, resolution will not be reattempted until the update interval of cloudera has elapsed or updates are forced -> [Help 1]
```
解决方法：
```
git clone https://github.com/julianhyde/eigenbase-properties.git
cd eigenbase-properties
mvn clean install
mvn install:install-file -DgroupId=eigenbase -DartifactId=eigenbase-properties -Dversion=1.1.4 -Dpackaging=jar -Dfile=/home/appuser/.m2/repository/net/hydromatic/eigenbase-properties/1.1.6-SNAPSHOT/eigenbase-properties-1.1.6-SNAPSHOT.jar
```

ll /home/appuser/.m2/repository/eigenbase/eigenbase-properties
ll /home/appuser/.m2/repository/net/hydromatic/eigenbase-properties/



vim ~/.bashrc
```
#HADOOP  
export HADOOP_HOME=/opt/cloudera/parcels/CDH/lib/hadoop
export HADOOP_CONF_DIR=/etc/hadoop/conf
#Hive
export HIVE_HOME=/opt/cloudera/parcels/CDH/lib/hive
export HIVE_CONF_DIR=/etc/hive/conf
#Spark
export SPARK_HOME=/opt/cloudera/parcels/SPARK2/lib/spark2
export SPARK_CONF_DIR=/etc/spark2/conf
export PYSPARK_ALLOW_INSECURE_GATEWAY=1  # Pyspark必须加的参数
```


xcall sudo yum install -y telnet expect dos2unix


检查spark的时候会报错。用的是spark-submit -version的命令。而部署在