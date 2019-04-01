# -*- encoding:utf-8 -*-
"""
20190325
    MR-寻找最高温度  map函数
"""
from __future__ import print_function
import re
import sys


for line in sys.stdin:
    val = line.strip()
    (year, temp, q) = (val[15:19], val[87:92], val[92:93])
    if (temp != "+9999" and re.match("[01459]", q)):
        print("%s\t%s" % (year, temp))
