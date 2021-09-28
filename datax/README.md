## DataX
基于datax的做了大幅改动的项目：[Addax](https://github.com/wgzhao/Addax)

可视化操作界面：[datax-web](https://github.com/WeiYe-Jing/datax-web)


### 安装
DataX和Addax的安装都很简单，解压安装包之后，直接使用
```
# DataX运行自检脚本
python /opt/app/datax/bin/datax.py /opt/app/datax/job/job.json
# Addax运行自检脚本
/opt/app/addax-4.0.3/bin/addax.sh /opt/app/addax-4.0.3/job/job.json
```

[datax-web部署过程](https://github.com/WeiYe-Jing/datax-web/blob/master/doc/datax-web/datax-web-deploy.md)
- 注意：执行器要指定的python_path是datax.py所在的地址
```
# vim /opt/app/datax-web-2.1.2/modules/datax-execute/bin/env.properties
## PYTHON脚本执行位置
#PYTHON_PATH=/home/hadoop/install/datax/bin/datax.py
PYTHON_PATH=/usr/local/datax/bin/datax.py
```
- 在Linux环境下使用JPS命令，查看是否出现DataXAdminApplication和DataXExecutorApplication进程，如果存在这表示项目运行成功

### 示例
#### 1. 从stream流读取数据并打印到控制台
配置文件参见：[stream2stream.json](jobconfig/stream2stream.json)

执行命令：
```
cd $DATAX_HOME
bin/datax.py /home/appuser/forlearn/datax/stream2stream.json

```

#### 2.读取MySQL中的数据存放到HDFS

查看官方模板
```
python /opt/app/datax/bin/datax.py -r mysqlreader -w hdfswriter
``` 
配置文件参见：[mysql2hdfs.json](jobconfig/mysql2hdfs.json)

执行命令：
```
datax.py /home/appuser/forlearn/datax/mysql2hdfs.json
```
> 注意：HdfsWriter实际执行时会在该文件名后添加随机的后缀作为每个线程写入实际文件名。

#### 3.从HDFS读取数据写入mysql
配置文件参见：[hdfs2mysql.json](jobconfig/hdfs2mysql.json)

#### 4.从Oracle中读取数据存到MySQL


#### 5.读取Oracle的数据存入HDFS中


#### 6.读取MongoDB的数据导入到HDFS

#### 7.读取MongoDB的数据导入MySQL


### data-web
访问地址：