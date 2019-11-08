## Spark环境搭建

#### 1. Local模式【本地部署】
解压后直接使用
```
tar -zxvf spark-2.3.3-bin-hadoop2.7.tgz
```
求PI的案例
```
# cd spark-2.3.3-bin-hadoop2.7

bin/spark-submit \
--class org.apache.spark.examples.SparkPi \
--executor-memory 1G \
--total-executor-cores 2 \
./examples/jars/spark-examples_2.11-2.3.3.jar \
100
```

#### 2. standalone模式【独立部署】
构建一个由Master+Slave构成的Spark集群，Spark运行在集群中。
```
# cd spark-2.3.3-bin-hadoop2.7/conf
# 修改slaves文件
mv slaves.template slaves
vim slaves
---------------
s01
s02
s03
---------------

# 修改spark-env.sh
vim spark-env.sh
---------------
SPARK_MASTER_HOST=s01
SPARK_MASTER_PORT=7077
---------------

# 分发spark包到其他机器上
xsync /opt/app/spark-2.3.3-bin-hadoop2.7

# 启动
sbin/start-all.sh
```

#### 3. 


#### 4. python等跟spark结合的环境搭建

[spark学习环境搭建.md](../../spark/spark学习环境搭建.md)