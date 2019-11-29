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
- 