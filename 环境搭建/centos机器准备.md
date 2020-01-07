## centos机器准备

使用centos7.7 mini版本
```
磁盘分区参考：
- 标准分区， 
- 挂载点 / 195G  ext4文件系统
- 挂载点 /boot 200M  ext4文件系统
- swap分区 4919M  swap文件类型

内存大小 8G/8G/8G 
```

安装之后的必要操作
```
# 0. 新建要给普通用户，能够sudo
useradd appuser
passwd appuser
vim /etc/sudoers
============= 添加下面内容 ===============
atguigu    ALL=(ALL)       NOPASSWD:ALL
============= 保存时wq!强制保存 ==========

# 1. 修改静态IP
cd  /etc/sysconfig/network-scripts/
vi ifcfg-ens33 
service network restart
# 查看IP地址是否生效
ip addr

# 2. 配置阿里镜像源
cd /etc/yum.repos.d/
mv CentOS-Base.repo CentOS-Base.repo.bak
wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
yum makecache

# 3.安装必要的安装包
yum install -y net-tools wget vim-enhanced links zip unzip gcc gcc-c++ lrzsz rsync

# 4. 关闭防火墙并不允许开机启动
systemctl stop firewalld
systemctl disable firewalld.service
# 5. 关闭selinux
vi /etc/selinux/config 
setenforce 0

# 6. 安装java8
mkdir -p /opt/software /opt/modules
chown -R appuser:appuser /opt/modules /opt/software/
ln -s /opt/modules/jdk1.8.0_231 /usr/local/jdk
vim /etc/profile
=============
#JAVA_HOME
export JAVA_HOME=/usr/local/jdk
export PATH=$PATH:$JAVA_HOME/bin
================
source /etc/profile
java -version
```

集群安装相关操作

配置免密登录：
```
分别以root和appuser的用户执行下面的命令
ssh-keygen -t rsa

ssh-copy-id hadoop101
ssh-copy-id hadoop102
ssh-copy-id hadoop103
```


准备两个sh脚本
- 文件目录同步分发。用于从hadoop101分发到其他机器
```
# 1 检查参数
pcount=$#
if((pcount==0));then
echo no args;
exit;
fi
# 2 获取文件名称
p1=$1;
fname=`basename $p1`
echo fname=$fname
# 3 获取上级目录到绝对路径
pdir=`cd -P $(dirname $p1);pwd`
echo pdir=$pdir
# 4 获取但钱用户
user=`whoami`
# 5 循环
for((host=102;host<104;host++));do
    echo ----------- hadoop$host -----------
    rsync -rvl $pdir/$fname $user@hadoop$host:$pdir
done
```
- 批量执行命令脚本
```
#!/bin/bash --login
pcount=$#
if((pcount==0));then
        echo no args;
        exit;
fi

#echo ------------- localhost ----------
#$@
for((host=101; host<=103; host++)); do
        echo ---------- hadoop$host ---------
        ssh hadoop$host "source /etc/profile;$@"
done
```