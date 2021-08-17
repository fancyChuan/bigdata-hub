## HBase优化

### 1. 高可用
RegionServer的高可用是由Master负责。master负责监控RegionServer的生命周期，均衡RegionServer的负载

### 2. 预分区
每一个region维护着startRow与endRowKey，如果加入的数据符合某个region维护的rowKey范围，则该数据交给这个region维护

> 自动切分分区：将当前分区的rowkey排序后，取start_rowkey和end_rowkey中间的rowkey，一分为二。目的是为了负载均衡，但是往往会适得其反

预分区的方式：
- 手动设定预分区
```
HBase> create 'staff1','info','partition1',SPLITS => ['1000','2000','3000','4000']
```
- 生成16进制序列预分区
```
create 'staff2','info','partition2',{NUMREGIONS => 15, SPLITALGO => 'HexStringSplit'}
```
> 注意这种方式，在插入一条数据时，rowkey需要先转为16进制，再插入
- 按照文件中设置的规则预分区
```
create 'staff3','partition3',SPLITS_FILE => 'splits.txt'
```
- 使用JavaAPI创建预分区
```
// 方式1
public void createTable(HTableDescriptor desc, byte [] startKey,
      byte [] endKey, int numRegions)
// 方式2
public void createTable(final HTableDescriptor desc, byte [][] splitKeys)

```

### 3. RowKey设计
设计rowkey的主要目的 ，就是让数据均匀的分布于所有的region中，在一定程度上防止数据倾斜

原则：
- rowkey作为数据的唯一主键，需要密切与业务相关，从业务中选取有代表性的字段作为rowkey
- 保证rowkey字段的唯一性、不重复性
- rowkey足够散列，负载均衡
- 让有业务关联的rowkey尽量分不到一个region中

方式：
- 生成随机数、hash、散列值
- 字符串反转
- 字符串拼接

### 4. 内存优化
HBase操作过程中需要大量的内存开销，毕竟Table是可以缓存在内存中的，一般会分配整个可用内存的70%给HBase的Java堆。但是不建议分配非常大的堆内存，
因为GC过程持续太久会导致RegionServer处于长期不可用状态，一般16~48G内存就可以了，如果因为框架占用内存过高导致系统内存不足，框架一样会被系统服务拖死
```
# hbase-env.sh
HBASE_HEAPSIZE
```
### 5. 基础优化
- 允许在HDFS的文件中追加内容：hdfs-site.xml、HBase-site.xml
    - 属性：dfs.support.append
    - 解释：开启HDFS追加同步，可以优秀的配合HBase的数据同步和持久化。默认值为true。
- 优化DataNode允许的最大文件打开数: hdfs-site.xml
    - 属性：dfs.datanode.max.transfer.threads
    - 解释：HBase一般都会同一时间操作大量的文件，根据集群的数量和规模以及数据动作，设置为4096或者更高。默认值：4096
- 优化延迟高的数据操作的等待时间：hdfs-site.xml
    - 属性：dfs.image.transfer.timeout
    - 解释：如果对于某一次数据操作来讲，延迟非常高，socket需要等待更长的时间，建议把该值设置为更大的值（默认60000毫秒），以确保socket不会被timeout掉。
- 优化数据的写入效率：mapred-site.xml
```
属性：
mapreduce.map.output.compress
mapreduce.map.output.compress.codec
解释：开启这两个数据可以大大提高文件的写入效率，减少写入时间。第一个属性值修改为true，第二个属性值修改为：org.apache.hadoop.io.compress.GzipCodec或者其他压缩方式。
```
- 设置RPC监听数量:HBase-site.xml
```
属性：HBase.regionserver.handler.count
解释：默认值为30，用于指定RPC监听的数量，可以根据客户端的请求数进行调整，读写请求较多时，增加此值。
```
- 优化HStore文件大小：HBase-site.xml
```
属性：HBase.hregion.max.filesize
解释：默认值10737418240（10GB），如果需要运行HBase的MR任务，可以减小此值，因为一个region对应一个map任务，如果单个region过大，会导致map任务执行时间过长。该值的意思就是，如果HFile的大小达到这个数值，则这个region会被切分为两个Hfile。
```
- 优化HBase客户端缓存 HBase-site.xml
```
属性：HBase.client.write.buffer
解释：用于指定HBase客户端缓存，增大该值可以减少RPC调用次数，但是会消耗更多内存，反之则反之。一般我们需要设定一定的缓存大小，以达到减少RPC次数的目的。
```
- 指定scan.next扫描HBase所获取的行数：HBase-site.xml
```
属性：HBase.client.scanner.caching
解释：用于指定scan.next方法获取的默认行数，值越大，消耗内存越大。
```
- flush、compact、split机制
```
当MemStore达到阈值，将Memstore中的数据Flush进Storefile；compact机制则是把flush出来的小文件合并成大的Storefile文件。split则是当Region达到阈值，会把过大的Region一分为二。
涉及属性：
即：128M就是Memstore的默认阈值
HBase.hregion.memstore.flush.size：134217728
即：这个参数的作用是当单个HRegion内所有的Memstore大小总和超过指定值时，flush该HRegion的所有memstore。RegionServer的flush是通过将请求添加一个队列，模拟生产消费模型来异步处理的。那这里就有一个问题，当队列来不及消费，产生大量积压请求时，可能会导致内存陡增，最坏的情况是触发OOM。
HBase.regionserver.global.memstore.upperLimit：0.4
HBase.regionserver.global.memstore.lowerLimit：0.38
即：当MemStore使用内存总量达到HBase.regionserver.global.memstore.upperLimit指定值时，将会有多个MemStores flush到文件中，MemStore flush 顺序是按照大小降序执行的，直到刷新到MemStore使用内存略小于lowerLimit
```

### 6. 布隆过滤器
