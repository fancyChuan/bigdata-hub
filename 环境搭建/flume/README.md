## Flume安装配置

1.上传安装包并解压
```
tar -zxvf apache-flume-1.7.0-bin.tar.gz -C /opt/app/
mv apache-flume-1.7.0-bin flume-1.7.0-bin
```
2.创建软链接
```
xcall sudo ln -s /opt/app/flume-1.7.0-bin /usr/local/flume
```
3.修改配置文件

```
mv flume-env.sh.template flume-env.sh
vim flume-env.sh

# 加上JAVA_HOME
export JAVA_HOME=/usr/local/jdk 
```
4.给集群其他机器分发
```
xsync /opt/app/flume-1.7.0-bin
```