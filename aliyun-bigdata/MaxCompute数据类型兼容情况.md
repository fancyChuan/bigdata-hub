## MaxCompute数据类型兼容情况
MaxCompute 2.0推出了兼容开源主流产品的2.0数据类型和Hive兼容数据类型两个数据类型版本。加上原有的1.0数据类型版本，目前Maxompute一共支持3个数据类型版本

MaxCompute设置数据类型版本属性的参数共有3个：
- odps.sql.type.system.odps2：MaxCompute 2.0数据类型版本的开关，属性值为True或False。
- odps.sql.decimal.odps2：MaxCompute 2.0的Decimal数据类型的开关，属性值为True或False。
- odps.sql.hive.compatible：MaxCompute Hive兼容模式（即部分数据类型和SQL行为兼容Hive）数据类型版本的开关，属性值为True或False。

#### 各版本几个参数的设置情况
- 1.0数据类型版本
```
setproject odps.sql.type.system.odps2=false; --关闭MaxCompute 2.0数据类型。
setproject odps.sql.decimal.odps2=false; --关闭Decimal 2.0数据类型。
setproject odps.sql.hive.compatible=false; --关闭Hive兼容模式。
```
> [1.0版本跟其他版本的区别](https://help.aliyun.com/document_detail/159542.html?spm=a2c4g.11186623.6.638.5267134d2mQgTD#title-kl4-qiy-cm5)
- 2.0数据类型版本
```
setproject odps.sql.type.system.odps2=true; --打开MaxCompute 2.0数据类型。
setproject odps.sql.decimal.odps2=true; --打开Decimal 2.0数据类型。
setproject odps.sql.hive.compatible=false; --关闭Hive兼容模式。
```
> [2.0版本与其他版本的区别](https://help.aliyun.com/document_detail/159541.html?spm=a2c4g.11186623.2.21.1faf44f63pCPBq#title-68s-4kx-ea9)

- Hive语法兼容版本
```
setproject odps.sql.type.system.odps2=true; --打开MaxCompute 2.0数据类型。
setproject odps.sql.decimal.odps2=true; --打开Decimal 2.0数据类型。
setproject odps.sql.hive.compatible=true; --打开Hive兼容模式。
```

> session级别与project级别
```
set odps.sql.type.system.odps2=true; -- session级别
setproject odps.sql.type.system.odps2=true; -- 项目级别
```


官方文档：
- [数据类型版本说明](https://help.aliyun.com/document_detail/27821.html)
- [组件与数据类型的兼容情况](https://help.aliyun.com/document_detail/159538.html)


和hive在使用上的区别：
- unixtime('','yyyyMMdd') 这个hive返回null，但是MaxCompute上回报错
- 
