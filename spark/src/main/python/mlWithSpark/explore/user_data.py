# -*- encoding: utf-8 -*-
"""
Created on 18:25 2019/4/30

@author: fancyChuan
@email: 1247375074@qq.com
"""

from mlWithSpark.utils import util

user_data = util.get_user_data()
print(user_data.first())

print user_data.count()

print(user_data.groupBy("gender").count().collect())