## Sentry 原理

sentry管理权限的映射路径：权限->角色->用户组->用户
- “权限->角色”通过grant/revoke的SQL语句实现
- “角色->用户组”通过grant/revoke的SQL语句实现
- “用户组->用户”通过hadoop自身的用户/组映射自动授权。

Hadoop本身的用户和组的关系，都是同步Linux系统中的，区别：
- HDFS中的超级用户组是supergroup
- Linux中默认没有supergoup这个组


将Linux系统的权限信息同步到HDFS
```
hdfs dfsadmin -refreshUserToGroupsMappings
```

#### Linux用户和用户组
```
# 查看appuser用户在哪个组
groups appuser
# 查看组下面有多少用户
cat /etc/group   # 查找相关的组，比如
    ------------------
    hive:x:979:impala,admin
    ai_group:x:1008:zhangsan,appuser
    ------------------
# 
```


groupadd supergroup


2，添加用户 添加用户：useradd -m -g 组 新建用户名 注意：-m 自动建立用户家目录； -g 指定用户所在的组，否则会建立一个和用户名同名的组 useradd -m -g deploy test deploy 为用户组，test1为用户名

```
[appuser@dxbigdata103 ~]$ id appuser
uid=1000(appuser) gid=1000(appuser) groups=1000(appuser),1007(ai_group)

passwd -l test1     //在root下，禁止test1用户修改密码的权限

passwd -d test1    //删除test1的密码
```






参考资料：
- 1.[linux下添加用户组和用户 - 八戒vs - 博客园](https://www.cnblogs.com/stronger-xsw/p/13652540.html)
- 2.[linux 中将用户添加到组的 4 个方法 - mouseleo - 博客园](https://www.cnblogs.com/mouseleo/p/11867107.html)
