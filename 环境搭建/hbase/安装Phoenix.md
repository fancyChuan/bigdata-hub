## 安装Phoenix


```
# 1.解压，并将安装包重命名
tar -zxvf apache-phoenix-4.14.2-HBase-1.3-bin.tar.gz -C /opt/app/
mv apache-phoenix-4.14.2-HBase-1.3-bin phoenix-4.14.2-HBase-1.3-bin
# 2.将安装包分发到hadoop102和103
xsync /opt/app/phoenix-4.14.2-HBase-1.3-bin
# 3.创建软链接到/usr/local/phoenix
xcall sudo ln -s /opt/app/phoenix-4.14.2-HBase-1.3-bin /usr/local/phoenix
# 4.修改环境变量
# sudo vim /etc/profile
export PHOENIX_HOME=/usr/local/phoenix
export PHOENIX_CLASSPATH=$PHOENIX_HOME
export PATH=$PATH:$PHOENIX_HOME/bin
# 5. 分发配置文件
sudo xsync /etc/profile
# 6. 将Phoenix的两个jar包创建到hbase/lib的软链接
xcall ln -s /usr/local/phoenix/phoenix-4.14.2-HBase-1.3-server.jar /usr/local/hbase/lib/phoenix-4.14.2-HBase-1.3-server.jar
xcall ln -s /usr/local/phoenix/phoenix-4.14.2-HBase-1.3-client.jar /usr/local/hbase/lib/phoenix-4.14.2-HBase-1.3-client.jar
# 7. 启动hadoop,zk,hbase后，启动Phoenix
bin/sqlline.py hadoop101,hadoop102,hadoop103:2181
```
