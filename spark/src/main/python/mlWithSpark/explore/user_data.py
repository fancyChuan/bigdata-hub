# -*- encoding: utf-8 -*-
"""
Created on 18:25 2019/4/30

@author: fancyChuan
@email: 1247375074@qq.com
@desc: 探索用户数据

总结：
    1. Dataset.collect() 得到的是一个以Row对象为元素的list
    2. user_data.groupBy("gender").count().collect() 得到 [Row(gender=u'F', count=273) ...]
    3. Row的属性可以通过row["gender"]或者row.gender 获取，也可以通过 row.__getattr__("count") 获取，因为count也是row的一个方法名，row.count得到的是函数而不是属性count
    4. Dataset.toDF() 得到一个对列名重命名的Dataset，而 Dataset.toPandas() 得到pd.DataFrame对象
"""

import matplotlib.pyplot as plt
from mlWithSpark.utils import util

user_data = util.get_user_data()
print "【首行数据】", user_data.first()
print "【数据量】", user_data.count()
user_genders = user_data.groupBy("gender").count().collect()
print "【性别统计】\n", user_genders
print "【职业统计】\n", user_data.toPandas().occupation.value_counts()  # 转为pd.DataFrame统计 todo：需要放到一台机子的内存放得下？是不是数据转到一台机器上处理？

print "=========使用select取出age这一列进行统计======="
user_ages = user_data.select("age").collect()
print u"数据类型：", type(user_ages)  # 类型为list
print u"元素类型：", type(user_ages[0]), u" 内容为：", user_ages[0]  # 元素类型为<class 'pyspark.sql.types.Row'>
user_ages_list = [i.age for i in user_ages]
plt.hist(user_ages_list, bins=20, normed=True)
fig = plt.gcf()
# fig.set_size_inches(16, 10)
plt.show()