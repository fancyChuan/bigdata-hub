## HDFS

java接口
- 从Hadoop URL读取数据
    - java自带的URL可以读取url的信息，new URL("hdfs://host/path"").openStream()
    - 默认读取不到hdfs，需要通过 URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory()) 参见[URLCat.java](https://github.com/fancyChuan/bigdata-learn/blob/master/hadoop/src/main/java/hdfs/URLCat.java)
    - 每个JVM只能执行一次 setURLStreamHandlerFactory()
- 通过FileSystem API读取数据
    - 直接使用InputStream
    - 使用FSDataInputStream
- 写入数据
    - FSDataOutputStream
- 目录
    - mkdirs(Path p)
- 查询文件系统
    - 文件元数据： FileStatus，封装了文件长度、块大小、副本、修改时间、所有者、权限等信息
    - 列出文件: listStatus()
        - FileStatus[] listStatus(Path p) 
        - FileStatus[] listStatus(Path p, PathFilter filter) 
        - FileStatus[] listStatus(Path[] ps, PathFilter filter) 
        - Hadoop的FileUtil中stat2Paths()可以把FileStatus[]转为Path[]
    - 文件模式：可以使用通配符来寻找需要执行的文件
        - FileStatus[] globStatus(Path pathPattern)
        - FileStatus[] globStatus(Path pathPattern, PathFilter filter)
        - 支持的通配符跟Unix系统一直
    - PathFilter对象： 以编程的方式控制通配符
        - 实现PathFilter接口的accept() 方法
    - 删除数据： delete(Path f, boolean recursive) recursive=true时，非空目录及其内容会被永久性删除
    

#### 剖析文件写入

![img](https://github.com/fancychuan/bigdata-learn/blob/master/hadoop/img/HDFS的写数据流程.png?raw=true)

注意：
- 第1步请求NN上传文件，NN会做一系列检查，比如目标文件是否已经存在，父目录是否存在，是否有权限等
- 第2步返回成功了之后才开始请求上传第一个block

#### 剖析文件读取

![img](https://github.com/fancychuan/bigdata-learn/blob/master/hadoop/img/HDFS的读数据流程.png?raw=true)

### 5. NameNode和SecondaryNameNode
HDFS对数据的一致性和安全性要求高。

#### 5.1 NN和2NN工作机制
SecondaryNameNode：专门用于FsImage和Edits的合并
- 元信息需要在内存里也需要在磁盘中备份：FsImage
- 同步更新FsImage效率过低，引入Edits（只追加，效率高）。每当元数据有变化的时候，修改内存中的元数据并追加到Edits中
- 长时间使用Edits会导致文件过大，需要定期合并，故映入2ndNameNode
> 持有化技术：
> - Redis：加载高效，生成慢           <==> hadoop 2NN
> - RDB：生成快，加载慢，安全性略低     <==> FsImage
> - AOF：实时操作，安全性高。资源占用多  <==> edits.log

![img](https://github.com/fancychuan/bigdata-learn/blob/master/hadoop/img/NameNode工作机制.png?raw=true)

先更新文件、在更新内存。为了安全性考虑。不如更新了内存，然后断电，那么数据就丢失了

#### 5.2 FsImage和Edits的解析
#### 5.3 checkpoint时间设置
- hdfs-default.xml 默认2NN一小时执行一次checkpoint
```
<property>
  <name>dfs.namenode.checkpoint.period</name>
  <value>3600</value>
</property>
```
- 一分钟检查一次操作次数
- 当操作次数达到100万时，2NN执行一次
```
<property>
  <name>dfs.namenode.checkpoint.txns</name>
  <value>1000000</value>
<description>操作动作次数</description>
</property>
<property>
  <name>dfs.namenode.checkpoint.check.period</name>
  <value>60</value>
<description> 1分钟检查一次操作次数</description>
</property >
```
#### 5.4 NN故障处理
