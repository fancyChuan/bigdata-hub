## Flink on Yarn 实战

使用方式：flink的安装包解压之后，即可直接使用，而不需要额外的配置。
参考：[部署说明文档](../环境搭建/flink/README.md#二、yarn模式)

几点结论：
- 1.yarn-session的方式，只能在运行了`yarn-session.sh -d`的机器上，才能通过命令行提交flink作业，因为flink run的时候需要根据`/tmp/.yarn-properties-appuser`来找到session
    - 使用 `echo "stop" | ./bin/yarn-session.sh -id application_1609324396857_95667` 可以优雅的停掉session，并且删除`/tmp/.yarn-properties-appuser`
    - 如果是`yarn application -kill application_1609324396857_95667`的话，那么`/tmp/.yarn-properties-appuser` 会保留
- 2.如果要在其他机器也能提交作业，那么可以把`/tmp/.yarn-properties-appuser`这个文件拷贝一份该机器上
- 3.当然，也可以在flink的界面上submit的方式提交。
> 注意，使用Per-Job-Cluster，则不能在界面上使用 submit 的方式提交

#### 使用yarn-session
- 启动yarn-session
```
yarn-session.sh -d -jm 1024 -tm 1024 -nm flinktest 
```
- 访问yarn的web界面，比如 http://hadoop102:8088
- 找到相应的application，并访问
- 通过web界面的"submit New Job"来提交新的作业

#### 使用yarn-cluster，即 pre-job-cluster
直接在提交作业的时候加上参数 `-m yarn-cluster`
```
flink run -m yarn-cluster  \
 -c cn.fancychuan.scala.quickstart.DataStreamWcApp \
 /home/appuser/forlearn/flink/flink-1.0-SNAPSHOT.jar \ 
 --host hadoop101 --port 7777
```