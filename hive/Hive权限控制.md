

```
#权限分为 SELECT ,INSERT ,ALL
#查看所有role
show roles;
 
#创建role 
create role role_name;
 
#删除role
drop role role_name;  
 
#将某个数据库读权限授予给某个role
GRANT SELECT ON DATABASE db_name TO ROLE role_name;
 
#将test 表的 S1 列的读权限授权给role_name （TABLE也可以不写）
GRANT SELECT(s1) ON TABLE test TO ROLE role_name;
 
#查看role_name 在数据库db_name中权限
SHOW GRANT ROLE role_name ON DATABASE db_name;
 
#查看role_name 在表test中的权限
SHOW GRANT ROLE role_name ON TABLE test;
 
#将role_name 权限给予user_name用户
GRANT ROLE role_name TO USER user_name;
 
#将role_name 权限给予user_group用户组
GRANT ROLE role_name TO GROUP user_group;
 
#查看某个用户下所有赋予的role权限
SHOW ROLE GRANT USER user_name;
 
#查看某个用户组下所有赋予的role权限
SHOW ROLE GRANT GROUP user_group;
 
#查看某个role下的权限信息
SHOW GRANT ROLE role_name;
 
#回收role_name对数据库db_name的SELECT 权限
REVOKE SELECT ON DATABASE db_name FROM ROLE role_name;
 
#回收role_name对表test的SELECT 权限  
revoke select on [table] test from role role_name;  
 
#回收某个group下role权限
REVOKE ROLE role_name [, role_name]  FROM GROUP （groupName) [,GROUP (groupName)]
```



https://blog.csdn.net/yancychas/article/details/84202400


