## 添加ElasticSearch服务

### 一、制作parcel包
#### 1. 下载ES安装包
这里选择6.8.10版本

下载地址：https://artifacts.elastic.co/downloads/elasticsearch-hadoop/elasticsearch-6.8.10.tar.gz
#运行一下下载编译依赖包
mkdir -p ~/github/cloudera
cd ~/github/cloudera
#下载cm_ext工具
git clone https://github.com/cloudera/cm_ext.git
cd cm_ext
#获取依赖包
mvn package
#执行此命令时会报commons-cli-1.3-cloudera-pre-r1439998.jar和commons-cli-1.3-cloudera-pre-r1439998.pom not found的错误
#原因:这两个包下载不到
#解决:到https://repository.cloudera.com/artifactory/ext-release-local/commons-cli/commons-cli/1.3-cloudera-pre-r1439998/下将这两个包下载下来,然后拷到~/.m2/repository/commons-cli/commons-cli/1.3-cloudera-pre-r1439998/目录下后再执行 mvn package

### 二、集成到Cloudera Manager中


### 参考资料
- [CDH6.2.x制作Elasticsearch的Parcel包和csd文件](https://blog.csdn.net/weixin_38023225/article/details/106467548)
- 
