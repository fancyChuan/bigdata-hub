## Spark集群

###


### 使用spark-submit部署应用
基本语法： bin/spark-submit [options] <app jar | python file> [app options]

spark-submit的一些常见标记[options]

标记 | 描述
--- | ---
--master | 表示要连接的集群管理器。这个标记可接收的选项如下
--deploy-mode | 选择在本地（客户端“client”）启动驱动器程序，还是在集群中的一台工作节点机器（集群“cluster”）上启动。在客户端模式下，spark-submit 会将驱动器程序运行在spark-submit 被调用的这台机器上。在集群模式下，驱动器程序会被传输并执行于集群的一个工作节点上。默认是本地模式
--class | 运行Java 或Scala 程序时应用的主类
--name | 应用的显示名，会显示在Spark 的网页用户界面中
--jars | 需要上传并放到应用的CLASSPATH 中的JAR 包的列表。如果应用依赖于少量第三方的JAR包，可以把它们放在这个参数里【jar包依赖】
--files | 需要放到应用工作目录中的文件的列表。这个参数一般用来放需要分发到各节点的数据文件
--py-files | 需要添加到PYTHONPATH 中的文件的列表。其中可以包含.py、.egg 以及.zip 文件。【python依赖】
--executor-memory | 执行器进程使用的内存量，以字节为单位。可以使用后缀指定更大的单位，比如“512m”（512 MB）或“15g”（15 GB）
--driver-memory | 驱动器进程使用的内存量，以字节为单位。可以使用后缀指定更大的单位，比如“512m”（512 MB）或“15g”（15 GB）



> -- master指示要连接的集群，可以接受的值

值 | 描述
--- | ---
spark://host:port | 连接到指定端口的Spark 独立集群上。默认情况下Spark 独立主节点使用7077 端口
mesos://host:port | 连接到指定端口的Mesos 集群上。默认情况下Mesos 主节点监听5050 端口
yarn | 连接到一个YARN集群。当在YARN 上运行时，需要设置环境变量HADOOP_CONF_DIR 指向Hadoop 配置目录，以获取集群信息
local |  运行本地模式，使用单核
local[N] | 运行本地模式，使用N个核心
local[*] | 运行本地模式，使用尽可能多的核心

