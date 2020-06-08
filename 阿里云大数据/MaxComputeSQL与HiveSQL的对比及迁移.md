## MaxComputeSQL与HiveSQL的对比及迁移


开启MaxCompute和Hive语法兼容
```
set odps.sql.hive.compatible=true;
```

开启MaxCompute2.0以支持复杂的数据类型
```
（session级别）
set odps.sql.type.system.odps2=true;
（project级别）
setproject odps.sql.type.system.odps2=true;
```


