## 在hue上使用Sentry管理hive的访问权限

首先需要先明确几个概念：
- hive role： Hive类似于数据库，本身提供了一套权限控制机制，跟mysql有点像。只不过hive的权限是授给角色的
- hive group：通常被指定拥有哪些 hive role
- hue group：通过操作系统上的用户组和Hive的用户组进行关联。因此，配置完Hue用户组后还需配置OS上的用户组（需要配置所有hiveserver2，hue，sentry所在的机器)
- hue username：需要和hue group保持相同
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
