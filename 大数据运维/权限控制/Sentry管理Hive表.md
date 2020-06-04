## 在hue上使用Sentry管理hive的访问权限

首先需要先明确几个概念：
- hive role： Hive类似于数据库，本身提供了一套权限控制机制，跟mysql有点像。只不过hive的权限是授给角色的
- hive group：通常被指定拥有哪些 hive role
- hue group：通过操作系统上的用户组和Hive的用户组进行关联。因此，配置完Hue用户组后还需配置OS上的用户组
（需要配置所有hiveserver2，hue，sentry所在的机器)
- hue username：用来登录hue的用户，跟Linux上的用户对应，需要和hue group保持相同

示例：创建一个对 test_db 库只读的用户 read_user，用户归属到组 read_group
```
# 1. 用admin用户登录hue，通过“Manage Users” 新建用户组read_group
# 2. 同样适用admin用户在hue上创建用户 read_user 并把用户放到组 read_group中
# 3. 在部署CDH的Linux机器上（主要是hiveserver2、hue、sentry所在的机器）新建组和用户
[以下命令需要在每个linux上执行]
groupadd read_group
useradd read_user  # 这里会在Linux上创建用户，可以不用设置密码，设置的密码也是Linux的密码，使用hive的时候不会用到
gpasswd -a read_user read_group # 把用户添加到组

# 4. 使用hive用户登录hue，之后点击“Security”，也就是需要访问 http://bigdata101:8888/security/hive
# 4.1 创建角色(hive里的角色） read_role
# 4.2 把角色 read_role 跟组read_group关联起来
# 4.3 给read_role添加test_db库的select权限 
```


```
grant role allsel to user allsel;   --将数据库hive 角色allsel赋予linux用户allsel
grant select on test  to user allsel;  --授予用户select权限，这样用户下的角色拥有同样权限
--权限收回
revoke all from user allsel;
--权限查看
show roles; --查看已经有的角色
show grant role allsel; --查看角色已有权限)
show grant role allsel on database test; --查看角色在test数据库的已有权限
```
