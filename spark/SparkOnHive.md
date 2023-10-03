# Spark On Hive

hive主要的两个功能：
- Hive server：将SQL翻译成MR并提交执行
- MetaStore服务：元数据管理服务

Spark是没有元数据服务的，因此可以和Hive的元数据服务配合使用
- SparkSQL代码中，表来自DataFrame注册，才能写SQL，DataFrame的信息足够Spark用来翻译RDD
- 如果不写代码，想直接使用SQL，这个时候spark就无法翻译了，因为没有元数据

因此配置上需要2点：
- MetaStore正常运行
- Spark知道MeteStore在哪里

配置步骤：
1. 在Spark conf目录下，配置hive-site.xml
2. 上传jdbc驱动到Spark live目录
3. 启动hive metastore
4. 启动Spark使用

