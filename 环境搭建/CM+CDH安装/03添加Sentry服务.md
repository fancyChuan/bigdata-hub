## CDH添加Sentry服务

#### 1.修改hive仓库目录的权限
```
$ sudo -u hdfs hdfs dfs -chmod -R 771 /user/hive/warehouse
$ sudo -u hdfs hdfs dfs -chown -R hive:hive /user/hive/warehouse
```
如果启用了Kerberos，必须使用 hdfs 用户 kinit 然后设置相应目录的权限。
```
sudo -u hdfs kinit -kt hdfs.keytab hdfs
sudo -u hdfs hdfs dfs -chmod -R 771 /user/hive/warehouse
sudo -u hdfs hdfs dfs -chown -R hive:hive /user/hive/warehouse
```

注意：
- 将 hive-site.xml中将 hive.warehouse.subdir.inherit.perms 设置为 true, 以便在 warehouse 目录上设置的权限度可以集成到子目录

#### 2. 禁用HiveServer2的模拟
```
hive.server2.enable.impersonation, hive.server2.enable.doAs 设置为false，也就是取消该配置项
```
> 具体怎么理解？？


#### 
```
<property>
  <name>sentry.hive.testing.mode</name>
  <value>true</value>
</property>
```
否则会报以下错误：
```
FAILED: InvalidConfigurationException hive.server2.authentication can't be none in non-testing mode
```


### 参考资料
- [CDH 配置 Sentry 服务 - 简书](https://www.jianshu.com/p/dfcad1a3044d)
- [hue集成sentry后添加用户操作流程 - 简书](https://www.jianshu.com/p/e0fc624e433d)
- [0033-如何在Hue中使用Sentry-Hadoop实操的博客-51CTO博客](https://blog.51cto.com/14049791/2320839)
- [0569-5.15.1-开启Sentry后LOAD DATA异常分析](https://cloud.tencent.com/developer/article/1419628)