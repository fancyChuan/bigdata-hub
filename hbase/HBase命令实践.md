
### HBase的使用
> hbase shell不支持中文

启动HBase
```
方式1：
bin/start-hbase.sh

方式2：
bin/hbase-daemon.sh start master
bin/hbase-daemon.sh statt regionserver
```
#### 表的操作
##### list
##### create
创建表时，需要指定表名和列族名，而且至少需要指定一个列族，没有列族的表是没有任何意义的。
创建表时，还可以指定表的属性，表的属性需要指定在列族上！
- 格式1（需要指定列族属性时）
```
create '表名', { NAME => '列族名1', 属性名 => 属性值}, {NAME => '列族名2', 属性名 => 属性值}, …
```
- 格式2（不需要指定列族属性，可以简写）
```
create'表名','列族名1' ,'列族名2', …
```
##### desc
##### disable
停用表以后，防止对表做维护的时候客户端依然可以持续写入数据到表
- 删除表前，先停用
- 对表中的列进行修改的时候，也需要先停用表
> is_disabled 命令可以判断表是否停用
##### enable
- enable ‘表名’用来启用表
- is_enabled ‘表名’用来判断一个表是否被启用
- enable_all ‘正则表达式’可以通过正则来过滤表，启用复合条件的表

##### exists
##### count
##### drop
需要先手动disable
##### truncate
会自动先disable
##### get_split
获取表所对应的Region个数
##### alter



#### 数据操作
##### scan命令
scan命令可以按照rowkey的字典顺序来遍历指定的表的数据。
- scan ‘表名’：默认当前表的所有列族。
- scan ‘表名’,{COLUMNS=> [‘列族:列名’],…} ： 遍历表的指定列
- scan '表名', { STARTROW => '起始行键', ENDROW => '结束行键' }：指定rowkey范围。如果不指定，则会从表的开头一直显示到表的结尾。区间为左闭右开。
- scan '表名', { LIMIT => 行数量}： 指定返回的行的数量
- scan '表名', {VERSIONS => 版本数}：返回cell的多个版本
> VERSIONS默认是1，需要通过alter修改后才能保存多个版本的数据
- scan '表名', { TIMERANGE => [最小时间戳, 最大时间戳]}：指定时间戳范围
  注意：此区间是一个左闭右开的区间，因此返回的结果包含最小时间戳的记录，但是不包含最大时间戳记录
- scan '表名', { RAW => true, VERSIONS => 版本数}
  显示原始单元格记录，在Hbase中，被删掉的记录在HBase被删除掉的记录并不会立即从磁盘上清除，而是先被打上墓碑标记，然后等待下次major compaction的时候再被删除掉。注意RAW参数必须和VERSIONS一起使用，但是不能和COLUMNS参数一起使用。
- scan '表名', { FILTER => "过滤器"} and|or { FILTER => "过滤器"}: 使用过滤器扫描

##### put命令
- put可以新增记录还可以为记录设置属性。
- put '表名', '行键', '列名', '值'
- put '表名', '行键', '列名', '值',时间戳
- put '表名', '行键', '列名', '值', { '属性名' => '属性值'}
- put '表名', '行键', '列名', '值',时间戳, { '属性名' =>'属性值'}

##### get命令
get是一种特殊的scan命令，支持scan的大部分属性，如COLUMNS，TIMERANGE，VERSIONS，FILTER

##### delete命令
删除某rowkey的全部数据：
```HBase(main):016:0> deleteall 'student','1001'```
删除某rowkey的某一列数据：
```HBase(main):017:0> delete 'student','1002','info:sex'```

> 表只有enable，才能做表的更改


### 关于VERSIONS
每个不同时间戳的cell就是一个版本，时间戳就是版本

- 列族的VERSIONS：表示数据从MemStore刷新到StoreFile的时候最多保留多少个版本。如下所示，student的info列族的VERSIONS就是1
```
hbase(main):006:0> create 'student','info','allinfo'
0 row(s) in 1.2330 seconds
hbase(main):108:0> describe 'student'
Table student is ENABLED                                                                                                                                                                  
student                                                                                                                                                                                   
COLUMN FAMILIES DESCRIPTION                                                                                                                                                               
{NAME => 'allinfo', BLOOMFILTER => 'ROW', VERSIONS => '1', IN_MEMORY => 'false', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', TTL => 'FOREVER', COMPRESSION => 'NONE', MI
N_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}                                                                                                  
{NAME => 'info', BLOOMFILTER => 'ROW', VERSIONS => '1', IN_MEMORY => 'false', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', TTL => 'FOREVER', COMPRESSION => 'NONE', MIN_V
ERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}                                                                                                     
2 row(s) in 0.0130 seconds

# 插入几条数据
put 'student', '1001', 'info:age', 1
put 'student', '1001', 'info:name', 'hbase'
put 'student', '1002', 'info:age', 22
put 'student', '1002', 'info:name', 'flink'
put 'student', '1003', 'info:name', 'bigdata'

# 将info列族的VERSIONS修改为3
hbase(main):109:0> alter 'student',{NAME=>'info',VERSIONS=>3}
Updating all regions with the new schema...
0/1 regions updated.
1/1 regions updated.
Done.
0 row(s) in 2.9670 seconds

hbase(main):110:0> describe 'student'
Table student is ENABLED                                                                                                                                                                  
student                                                                                                                                                                                   
COLUMN FAMILIES DESCRIPTION                                                                                                                                                               
{NAME => 'allinfo', BLOOMFILTER => 'ROW', VERSIONS => '1', IN_MEMORY => 'false', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', TTL => 'FOREVER', COMPRESSION => 'NONE', MI
N_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}                                                                                                  
{NAME => 'info', BLOOMFILTER => 'ROW', VERSIONS => '3', IN_MEMORY => 'false', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', TTL => 'FOREVER', COMPRESSION => 'NONE', MIN_V
ERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}                                                                                                     
2 row(s) in 0.0220 seconds
```
- 修改之后，MemStore进行flush操作的时候，最多会有三个最新版本的数据会flush到StoreFile中
- 执行scan命令的时候指定查询的版本号，表示最多显示多少个版本的数值，这些数值的来源有：StoreFile以及MemStore
```
scan 'student', {RAW=>true, VERSIONS=>10}
# 结果是，最多有3个来源于StoreFile，其余最多7个版本来源于MemStore
```
> 每put一次就会在MemStore新增一个版本的数据


```
# 对rowkey=1001的info:age更新两次数据
hbase(main):011:0> put 'student', '1001', 'info:age', 1
0 row(s) in 0.0810 seconds

hbase(main):012:0> put 'student', '1001', 'info:age', 2
0 row(s) in 0.0100 seconds
# 只能查到最新version的内容
hbase(main):013:0> get 'student', '1001'
COLUMN         CELL                                   
 info:age      timestamp=1628583509482, value=2       
 info:name     timestamp=1621056641969, value=boy     
1 row(s) in 0.0190 seconds
# 查看3个版本的内容
hbase(main):041:0> get 'student', '1001', {COLUMN=>'info:age',VERSIONS=>3}
COLUMN         CELL                                   
 info:age      timestamp=1628583509482, value=2       
 info:age      timestamp=1628583506150, value=1       
 info:age      timestamp=1621086119700, value=88      
1 row(s) in 0.0020 seconds

# 将数据删除后，会打上标识 type=DeleteColumn
hbase(main):043:0> delete 'student', '1001','info:age'
0 row(s) in 0.0180 seconds

hbase(main):047:0> scan 'student', {LIMIT=>2, RAW=>true, VERSIONS=>3}
ROW            COLUMN+CELL                            
 1001          column=info:age, timestamp=162858401661
               4, type=DeleteColumn                   
 1001          column=info:age, timestamp=162858350948
               2, value=2                             
 1001          column=info:age, timestamp=162858350615
               0, value=1                             
 1001          column=info:age, timestamp=162108611970
               0, value=88                            
 1001          column=info:name, timestamp=16210566419
               69, value=boy                          
 1002          column=info:name, timestamp=16210567151
               60, value=fancy                        
2 row(s) in 0.0070 seconds
```
