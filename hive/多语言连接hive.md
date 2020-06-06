## 多语言连接hive

python访问Hive
- 使用pyhive
```
from pyhive import hive

conn = hive.Connection(host='192.168.0.1',
                       port=10000,
                       auth="CUSTOM",
                       database='gld',
                       username='hive',
                       password='hive')
cursor = conn.cursor()
cursor.execute('select * from student limit 10')
for result in cursor.fetchall():
    print(result)
cursor.close()
conn.close()
```
- 使用impyla
```
from impala.dbapi import connect
conn = connect(host='bigdata101', port=10000, database='wcl_dwh', user='username', password='xxxx', auth_mechanism="PLAIN")
cursor = conn.cursor()
cursor.execute('select * from wcl_dwh.dwd_card_txn_hs limit 10')

# 获取所有的数据
data = cursor.fetchall()
# 使用完记得关闭连接
cursor.close()
conn.close()

```





#### 参考资料
- [Windows下安装sasl - 简书](https://www.jianshu.com/p/c67657db5a93)
- [python连接hive （安装impyla）的采坑之旅](https://www.cnblogs.com/free-easy0000/p/9638982.html)