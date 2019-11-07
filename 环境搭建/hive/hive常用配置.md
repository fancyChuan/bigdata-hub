
### hive-site.xml
- 命令行显示当前数据库
```
<property>
	<name>hive.cli.print.header</name>
	<value>true</value>
</property>
<property>
	<name>hive.cli.print.current.db</name>
	<value>true</value>
</property>

```
- hive thrift服务
```xml
  <property>
    <name>hive.metastore.uris</name>
    <value>thrift://hadoop03:9083</value>
  </property>
```
- hive数据在HDFS上的存储位置
```xml
  <property>
    <name>hive.metastore.warehouse.dir</name>
    <value>/user/hive/warehouse</value>
  </property>
```
- hiveserver2操作日志位置
```xml
<property><!-- TODO:操作日志干嘛用的？？ -->
    <name>hive.server2.logging.operation.log.location</name>
    <value>/var/log/hive/operation_logs</value>
</property>
```
- hive计算引擎
```xml
  <property>
    <name>hive.execution.engine</name>
    <value>mr</value>
  </property>
```
- hive merge file
```
  <property>
    <name>hive.merge.mapfiles</name>
    <value>true</value>
  </property>
  <property>
    <name>hive.merge.mapredfiles</name>
    <value>false</value>
  </property>
  <property>
    <name>hive.merge.sparkfiles</name>
    <value>true</value>
  </property>
```

- hive下spark相关配置
```
  <property>
    <name>spark.executor.memory</name>
    <value>912680550</value>
  </property>
  <property>
    <name>spark.driver.memory</name>
    <value>966367641</value>
  </property>
  <property>
    <name>spark.executor.cores</name>
    <value>4</value>
  </property>
  <property>
    <name>spark.yarn.driver.memoryOverhead</name>
    <value>102</value>
  <property>
    <name>spark.driver.memory</name>
    <value>966367641</value>
  </property>
  <property>
    <name>spark.executor.cores</name>
    <value>4</value>
  </property>
  <property>
    <name>spark.yarn.driver.memoryOverhead</name>
    <value>102</value>
  </property>
  <property>
    <name>spark.yarn.executor.memoryOverhead</name>
    <value>153</value>
  </property>
  <property>
    <name>spark.dynamicAllocation.enabled</name>
    <value>true</value>
  </property>
  <property>
    <name>spark.dynamicAllocation.initialExecutors</name>
    <value>1</value>
  </property>
  <property>
    <name>spark.dynamicAllocation.minExecutors</name>
    <value>1</value>
  </property>
  <property>
    <name>spark.dynamicAllocation.maxExecutors</name>
    <value>2147483647</value>
  </property>
```