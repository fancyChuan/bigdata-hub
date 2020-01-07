## Ambari+HDP安装

Ambari的功能
- 提供了跨任意数量的主机安装Hadoop服务的分步向导。
- 处理群集的Hadoop服务配置。
- 提供集中管理，用于在整个集群中启动，停止和重新配置Hadoop服务。
- 提供了一个仪表板，用于监控Hadoop集群的运行状况和状态。
- 利用Ambari指标系统进行指标收集。
- 利用Ambari Alert Framework进行系统警报，并在需要您注意时通知您（例如，节点出现故障，剩余磁盘空间不足等）

### 版本
Ambari2.6.

资料地址
- 官网所有文档： https://docs.cloudera.com/HDPDocuments/
- Ambari下载地址：https://docs.cloudera.com/HDPDocuments/Ambari-2.6.2.2/bk_ambari-installation/content/ambari_repositories.html
- HDP下载地址：https://docs.cloudera.com/HDPDocuments/Ambari-2.6.2.2/bk_ambari-installation/content/hdp_stack_repositories.html

这里选择的版本是centos7+ambari2.6.2.2+hdp2.6.5.0
- ambari下载地址: [ambari2.6.2.2](http://public-repo-1.hortonworks.com/ambari/centos7/2.x/updates/2.6.2.2/ambari-2.6.2.2-centos7.tar.gz)
- hdp下载地址: [HDP-2.6.5.0](http://public-repo-1.hortonworks.com/HDP/centos7/2.x/updates/2.6.5.0/HDP-2.6.5.0-centos7-rpm.tar.gz)
- hdp-utils下载地址：[HDP-UTILS](http://public-repo-1.hortonworks.com/HDP-UTILS-1.1.0.22/repos/centos7/HDP-UTILS-1.1.0.22-centos7.tar.gz)


### 安装步骤
