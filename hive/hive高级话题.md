## hive高级话题

### hive锁
有两种：
- 共享锁shared（S） 触发后仍可以支持并发执行 todo:为什么要加共享锁？
- 互斥锁exclusive（X） 只要触发了这种锁，该表或分区不能并发执行作业

hivesql | 对应的锁情况
--- | ---
select .. T1 partition P1 | S on T1, T1.P1
insert into T2(partition P2) select .. T1 partition P1 | S on T2, T1, T1.P1 and X on T2.P2
insert into T2(partition P.Q) select .. T1 partition P1 | S on T2, T2.P, T1, T1.P1 and X on T2.P.Q
alter table T1 rename T2 | X on T1
alter table T1 add cols | X on T1
alter table T1 replace cols | X on T1
alter table T1 change cols | X on T1
alter table T1 add partition P1 | S on T1, X on T1.P1
alter table T1 drop partition P1 | S on T1, X on T1.P1
alter table T1 touch partition P1 | S on T1, X on T1.P1
*alter table T1 set serdeproperties * | S on T1
*alter table T1 set serializer * | S on T1
*alter table T1 set file format * | S on T1
*alter table T1 set tblproperties * | X on T1
drop table T1 | X on T1

注意：
- load data [local] inpath '' into table xx partition 也会触发锁，触发的锁同insert
- 使用hadoop fs -put xx yy 命名上传数据不会触发锁，所以可以用这个命令来代替load data避免锁，但需要先建分区

查看锁的命令：
```
SHOW LOCKS； -- 查看当前所有锁
SHOW LOCKS <TABLE_NAME>;
SHOW LOCKS <TABLE_NAME> extended;
SHOW LOCKS <TABLE_NAME> PARTITION (<PARTITION_DESC>);
SHOW LOCKS <TABLE_NAME> PARTITION (<PARTITION_DESC>) extended;
```

- 自定义权限校验
    - hive支持多种权限校验的方式，比如NONE,NOSASL, KERBEROS, LDAP, PAM ,CUSTOM等等
    - CUSTOM则是自定义权限校验，实现步骤如下：
        - 开发一个类，实现org.apache.hive.service.auth.PasswdAuthenticationProvider接口，并重写Authenticate方法
        - 将自定义的类达成jar包分发到集群的hive/lib目录下
        - 在配置文件中开启自定义权限校验，并配置hive.server2.custom.authentication.clas指向自定义的权限校验类
```
<property>
    <name>hive.server2.authentication</name>
    <value>CUSTOM</value>
</property>
<property>
    <name>hive.server2.custom.authentication.class</name>
    <value>cn.fancychuan.hive.auth.CustomHiveServer2Auth</value>
</property>
```