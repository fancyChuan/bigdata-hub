## 安装spark2.x



http://archive.cloudera.com/spark2/parcels/2.3.0.cloudera3/

http://archive.cloudera.com/spark2/csd/



spark2的位置：
```
cd /opt/cloudera/parcels/SPARK2-2.3.0.cloudera3-1.cdh5.13.3.p0.458809/lib/spark2

# 计算下Π
bin/spark-submit --class org.apache.spark.examples.SparkPi --master yarn --deploy-mode client ./examples/jars/spark-examples_2.11-2.3.0.cloudera3.jar 100
```


参考文档：https://www.jianshu.com/p/170ffe85c063/