## 待研究的大数据问题

- 用最高权限用户为什么会提示没有权限
> 系统架构 hive + 自定义权限校验 + sentry
```
[2019-12-07 01:20:57,095] [INFO] - hive中执行SQL语句,执行的命令为:
beeline -u "jdbc:hive2://hadoop02:10000/default" -n hadoop -p hadoop -e "ALTER TABLE provider_api_db.tio_decision_credit_data_auto DROP IF EXISTS PARTITION(data_date='2019-12-06')" 
[2019-12-07 01:22:29,457] [WARNING] - ===============================
[2019-12-07 01:22:29,457] [WARNING] - 第0次命令执行失败，日志为：
 
Java HotSpot(TM) 64-Bit Server VM warning: ignoring option MaxPermSize=512M; support was removed in 8.0
which: no hbase in (/usr/bin:/bin)
Java HotSpot(TM) 64-Bit Server VM warning: ignoring option MaxPermSize=512M; support was removed in 8.0
scan complete in 2ms
Connecting to jdbc:hive2://hadoop02:10000/default
Connected to: Apache Hive (version 1.1.0-cdh5.10.2)
Driver: Hive JDBC (version 1.1.0-cdh5.10.2)
Transaction isolation: TRANSACTION_REPEATABLE_READ
Error: Error while compiling statement: FAILED: SemanticException No valid privileges
 User hadoop does not have privileges for ALTERTABLE_DROPPARTS
 The required privileges: Server=server1->Db=provider_api_db->Table=tio_decision_credit_data_auto->action=*; (state=42000,code=40000)
Closing: 0: jdbc:hive2://hadoop02:10000/default

```
- 没有MR任务的时候HDFS IO为什么也很高
- 集群数据量不是特别大，CPU的资源不是能很好利用的时候是不是可以把HDFS的块将为64M，这样能提高并发量。该证明测试性能和差别呢？
- HDFS的备份数是不是对性能也有影响呢？比如一份数据都在一个节点上，就会导致要么发生网络传输，要么就在一台机器上跑完。
- 为什么文件在本地文件系统，通过load命令始终不成功？总是报找不到文件？

- 频繁的清理buff/cache，会不会对大数据的任务产生影响？
```
*/60 * * * * sh /root/clean_cache.sh
# clean_cache.sh 内容如下
echo 1 > /proc/sys/vm/drop_caches;
echo 2 > /proc/sys/vm/drop_caches;
echo 3 > /proc/sys/vm/drop_caches;
```

- 自己上传的lzo文件需要手动创建索引，那集群自己生成的文件，还需要手动去创建索引吗？
