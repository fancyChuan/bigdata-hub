## MySQL多版本多实例安装指南

### 01通过rpm包的方式安装
下载rpm包地址： http://mirrors.sohu.com/mysql/MySQL-5.6/
官方下载地址： https://downloads.mysql.com/archives/community/


安装mysql
```
[root@s03 softwares]$ rpm -qa | grep mysql
mysql-libs-5.1.66-2.el6_3.x86_64
[root@s03 softwares]$ rpm -e --nodeps mysql-libs-5.1.66-2.el6_3.x86_64

[root@s00 mysql-libs]# rpm -ivh MySQL-server-5.6.24-1.el6.x86_64.rpm 

[root@s00 mysql-libs]# rpm -ivh MySQL-client-5.6.24-1.el6.x86_64.rpm 
```
启动msyql之后
```
[root@s03 mysql-libs]# cat /root/.mysql_secret
# The random password set for the root user at Sat Nov 24 16:33:48 2018 (local time): ISMG0O1gZuPvn8cr
[root@s03 mysql-libs]# mysql -uroot -pISMG0O1gZuPvn8cr
# 第一次要修改密码
mysql> set PASSWORD=PASSWORD('123456');
# 允许所有ip访问
mysql> update user set host='%' where user='root' and host='localhost' ;
# 查看权限情况
mysql> select user, host, password from user;
# 刷新权限
mysql> flush privileges;
```

### 02多版本多实例共存
查看整理的博文：
- [win10同时安装mysql5.6、mysql5.7以及mysql8多个版本_fancychuan的博客-CSDN博客](https://blog.csdn.net/fancychuan/article/details/121059475)
- [Centos7安装mysql5.6、mysql5.7和mysql8多个版本并共存_fancychuan的博客-CSDN博客](https://blog.csdn.net/fancychuan/article/details/121066434)