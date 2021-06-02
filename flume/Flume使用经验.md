## Flume使用经验

> flume-ng运行时，是会动态刷新agent配置文件的，也就是说修改了conf，可以不用重启flume-ng

### 1. Source
####（1）Taildir Source相比Exec Source、Spooling Directory Source的优势
- TailDir Source：断点续传、多目录。Flume1.6以前需要自己自定义Source记录每次读取文件位置，实现断点续传。
- Exec Source可以实时搜集数据，但是在Flume不运行或者Shell命令出错的情况下，数据将会丢失。
> 也就是ExecSource和其他异步Source一样，在channel容量满了以后或者出现异常的时候，客户端并无法知晓并暂停生成数据，也没有相应的缓存机制，由此会丢数据

> 解决思路：使用SpoolingDirSource或者TailDirSource，或者根据Flume的SDK自己实现
- Spooling Directory Source监控目录，不支持断点续传。
#### （2）batchSize大小如何设置？
答：Event 1K左右时，500-1000合适（默认为100）

### 2. Channel
采用Kafka Channel，省去了Sink，提高了效率。KafkaChannel数据存储在Kafka里面，所以数据是存储在磁盘中。

注意在Flume1.7以前，Kafka Channel很少有人使用，因为发现parseAsFlumeEvent这个配置起不了作用。也就是无论parseAsFlumeEvent配置为true还是false，都会转为Flume Event。
这样的话，造成的结果是，会始终都把Flume的headers中的信息混合着内容一起写入Kafka的消息中，这显然不是我所需要的，我只是需要把内容写入即可


### 3.其他经验
1、默认jvm环境只使用了20m内存，需要调整，扩大到2G基本够用。

2、配置组策略，即多个sink组成一组的传输效率远不如多个sink各传各的，因为一个组策略只相当于一个传输线程，而多个sink无组时可以每个sink相当于一个传输线程。

3、在无sink组情况下，增加sink个数可以提高传输速率，但是并不能无限增长。

4、memory channel对cpu、内存会有更高的要求，在我测试的时间内，cpu和内存使用率都在90左右，当然这也带来了比file channel更快的速度。

5、在网络带宽受限的情况下，增加sink、使用memory channel等方法都不能增加传输效率。

6、影响flume传输性能的主要因素有，jvm内存、网络带宽、channel类型、sink个数、机器硬件性能，至于压缩，我并没有测试，暂且保留。
