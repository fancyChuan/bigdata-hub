## 安装spark2.x


下载地址：

http://archive.cloudera.com/spark2/parcels/2.3.0.cloudera3/

http://archive.cloudera.com/spark2/csd/

#### 安装的关键过程
- 把spark的两个csd相关的jar包，放到 /opt/clouder/csd/ 下
- 把parcel包和哈希文件放到 /opt/cloudera/parcel-repo
- 把spark2 parcel包的mainfest.json 合并到 /opt/cloudera/parcel-repo/manifest.json 中，或者备份后直接替换
- 重启cm的server 和 agent

参考文档：https://www.jianshu.com/p/170ffe85c063/


#### 测试
spark2的位置：
```
cd /opt/cloudera/parcels/SPARK2-2.3.0.cloudera3-1.cdh5.13.3.p0.458809/lib/spark2

# 计算下Π
bin/spark-submit --class org.apache.spark.examples.SparkPi --master yarn --deploy-mode client ./examples/jars/spark-examples_2.11-2.3.0.cloudera3.jar 100
```


