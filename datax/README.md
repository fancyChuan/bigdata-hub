## DataX

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
data.py /home/appuser/forlearn/datax/mysql2hdfs.json
```
> 注意：HdfsWriter实际执行时会在该文件名后添加随机的后缀作为每个线程写入实际文件名。

#### 3.从HDFS读取数据写入mysql
配置文件参见：[hdfs2mysql.json](jobconfig/hdfs2mysql.json)

#### 4.从Oracle中读取数据存到MySQL


#### 5.读取Oracle的数据存入HDFS中


#### 6.读取MongoDB的数据导入到HDFS

#### 7.读取MongoDB的数据导入MySQL