## HDFS文件写入读取细节剖析

### 剖析文件写入

![img](https://github.com/fancychuan/bigdata-learn/tree/master/hadoop/img/HDFS的写数据流程.png?raw=true)

注意：
- 第1步请求NN上传文件，NN会做一系列检查，比如目标文件是否已经存在，父目录是否存在，是否有权限等
- 第2步返回成功了之后才开始请求上传第一个block

### 剖析文件读取

![img](https://github.com/fancychuan/bigdata-learn/tree/master/hadoop/img/HDFS的读数据流程.png?raw=true)