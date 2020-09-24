## 添加ElasticSearch服务

### 一、制作parcel包
#### 1. 下载ES安装包
官网下载地址：https://www.elastic.co/cn/downloads/elasticsearch

这里选择6.8.10版本

#### 2.下载cm_ext
Cloudera提供的cm_ext工具,对生成的csd和parcel进行校验
```
mkdir -p ~/github/cloudera
cd ~/github/cloudera
#下载cm_ext工具
git clone https://github.com/cloudera/cm_ext.git
cd cm_ext
#获取依赖包
mvn package -Dmaven.test.skip=true
```
> 可能会报错：找不到commons-cli-1.3-cloudera-pre-r1439998.jar和commons-cli-1.3-cloudera-pre-r1439998.pom
> 解决步骤：
> 1. 依赖包下载地址https://repository.cloudera.com/artifactory/ext-release-local/commons-cli/commons-cli/1.3-cloudera-pre-r1439998/
> 2. 拷到~/.m2/repository/commons-cli/commons-cli/1.3-cloudera-pre-r1439998/目录下后再执行 mvn package

#### 3.下载制作Parcel包和CSD文件的脚本
```
cd ~/github/cloudera
git clone https://github.com/ibagomel/elasticsearch-parcel.git
```
#### 4. 制作 Elasticsearch 的Parcel包和CSD文件并校验
```
cd cd ~/github/cloudera/elasticsearch-parcel
# 制作parcel包
POINT_VERSION=5 VALIDATOR_DIR=/home/appuser/github/cloudera/cm_ext OS_VER=el7 PARCEL_NAME=ElasticSearch ./build-parcel.sh /home/appuser/elasticsearch-6.8.10.tar.gz
# 制作CSD文件
VALIDATOR_DIR=/home/appuser/github/cloudera/cm_ext CSD_NAME=ElasticSearch ./build-csd.sh
# 查看生成的包和文件
cd ~/github/cloudera/elasticsearch-parcel
[appuser@bigdata104 elasticsearch-parcel]$ ll build-parcel
total 146676
drwxrwxr-x 9 appuser appuser      4096 Jun 24 00:40 ELASTICSEARCH-0.0.5.elasticsearch.p0.5
-rw-rw-r-- 1 appuser appuser 150181378 Jun 24 00:40 ELASTICSEARCH-0.0.5.elasticsearch.p0.5-el7.parcel
-rw-rw-r-- 1 appuser appuser       482 Jun 24 00:40 manifest.json
[appuser@bigdata104 elasticsearch-parcel]$ ll build-csd
total 8
-rw-rw-r-- 1 appuser appuser 5426 Jun 24 08:32 ELASTICSEARCH-1.0.jar
```

### 二、集成到Cloudera Manager中
参考下面的资料

注意：安装部署了之后再启动elasticsearch服务的时候会报错文件句柄太少的错误
```
报错：-bash: ulimit: open files: cannot modify limit: Operation not permitted

max file descriptors [65535] for elasticsearch process is too low
```

解决方法：vim /etc/security/limits.conf 填入以下内容
```
elasticsearch hard nofile 65536
elasticsearch soft nofile 65536
```

### 参考资料
- [CDH6.2.x制作Elasticsearch的Parcel包和csd文件](https://blog.csdn.net/weixin_38023225/article/details/106467548)
- [CDH6安装部署ES服务](https://blog.csdn.net/weixin_38023225/article/details/106468391)
- [解决：max file descriptors [65535] for elasticsearch process is too low_春风化雨](https://blog.csdn.net/jiahao1186/article/details/90235771])

