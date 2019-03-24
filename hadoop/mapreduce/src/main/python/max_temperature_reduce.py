# -*- encoding:utf-8 -*-
"""
20190325
    MR-寻找最高温度  reduce函数

其实这个算法有点问题，但mapper传过来的数据没有排序过的话，这个reducer就不能准确找出每个年份的最大值
"""
import sys

(last_key, max_val) = (None, -10000)

for line in sys.stdin:
    (key, val) = line.strip().split("\t")
    if last_key and last_key != key:
        print("%s\t%s" % (last_key, max_val))
        (last_key, max_val) = (key, int(val))
    else:
        (last_key, max_val) = (key, max(max_val, val))
if last_key:
    print("%s\t%s" % (last_key, max_val))
