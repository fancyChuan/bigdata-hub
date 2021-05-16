## TODO

- metastore服务的端口是如何修改的？hive-site.xml中的
```
<property>
  <name>hive.metastore.uris</name>
  <value>thrift://hadoop101:9083</valu>
</property>
```
实际是面向客户端使用的，告诉客户端或者hiveserver2需要到哪里去找到metastore