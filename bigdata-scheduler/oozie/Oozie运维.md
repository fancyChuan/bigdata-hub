## Oozie运维

#### 1.时区统一
- 修改hue时区为Asia/Shanghai
```
# cm上搜索time_zone
# 改为 Asia/Shanghai
```
- 修改Oozie时区
```
# 1.登录Cloudera Manager 进入Ooize服务的配置界面搜索“oozie-site.xml”
# 2. 增加配置
<property>
    <name>oozie.processing.timezone</name>
    <value>GMT+0800</value>
</property>
```
- 重启重新部署
