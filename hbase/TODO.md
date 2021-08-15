## TODO

- 体会HBase关于MemStore刷写机制的参数配置，与heapsize等JVM配置的关系，包括StoreFile合并的机制


- 体会Region切分的策略设计理念以及妙处

- 为什么HBase的API将大量的方法设置为私有的，使用起来似乎很麻烦，有什么好处？


#### 其他
使用zerotier构建的局域网，win7想访问win10创建出来的虚拟机集群的hbase，会访问不了

而通过win10本地去访问集群是可以的。为什么会出现这种情况？

进一步测试，发现用无法使用Telnet 访问hbase的16000端口
> 可能原因：Master创建的时候，直接绑定了虚拟机的ip，那么通过zerotier创建的ip来访问的时候，就无法连接上，虽然ping也能成功
```
[appuser@hadoop102 ~]$ telnet 192.168.191.99 16000
Trying 192.168.191.99...
telnet: connect to address 192.168.191.99: Connection refused
[appuser@hadoop102 ~]$ ping 192.168.191.99
PING 192.168.191.99 (192.168.191.99) 56(84) bytes of data.
64 bytes from 192.168.191.99: icmp_seq=1 ttl=64 time=0.477 ms
64 bytes from 192.168.191.99: icmp_seq=2 ttl=64 time=0.587 ms
64 bytes from 192.168.191.99: icmp_seq=3 ttl=64 time=1.00 ms
64 bytes from 192.168.191.99: icmp_seq=4 ttl=64 time=3.20 ms
^C
--- 192.168.191.99 ping statistics ---
4 packets transmitted, 4 received, 0% packet loss, time 3006ms
rtt min/avg/max/mdev = 0.477/1.318/3.201/1.104 ms
[appuser@hadoop102 ~]$ telnet 192.168.191.99 16000
Trying 192.168.191.99...
telnet: connect to address 192.168.191.99: Connection refused
[appuser@hadoop102 ~]$ 
[appuser@hadoop102 ~]$ 
[appuser@hadoop102 ~]$ 
[appuser@hadoop102 ~]$ 
[appuser@hadoop102 ~]$ telnet 192.168.191.99 22
Trying 192.168.191.99...
Connected to 192.168.191.99.
Escape character is '^]'.
SSH-2.0-OpenSSH_7.4

Protocol mismatch.
Connection closed by foreign host.
[appuser@hadoop102 ~]$ telnet hadoop101 16000     
Trying 192.168.133.161...
Connected to hadoop101.
Escape character is '^]'.


4org.apache.hadoop.hbase.ipc.FatalConnectionExceptionTExpected HEADER=HBas but received HEADER=\x0D\x0A\x0D\x0A from 192.168.133.162:51344(Connection closed by foreign host.
```