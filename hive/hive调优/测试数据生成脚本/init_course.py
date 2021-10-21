# coding: utf-8
import random, datetime
import sys
reload(sys)
sys.setdefaultencoding('utf8')
#创建一个函数，参数start表示循环的批次
def create_student_sc_dict(start):
    filename = str(start)+'.txt'
    print filename
    with open('./init_course/'+filename , mode='w') as fp:
        for i in xrange(start * 40000, (start + 1) * 40000):
            #课程出现越多表示喜欢的人越多
            course = random.sample([u'数学', u'数学', u'数学', u'数学', u'数学',
            u'语文', u'英语', u'化学', u'物理', u'生物'], 1)[0]
            model = {"s_no": u"xuehao_no_" + str(i),
                     "course": u"{0}".format(course),
                     "op_datetime": datetime.datetime.now().strftime("%Y-%m-%d"),
                     "reason": u"我非常非常非常非常非常非常非常"
                               u"非常非常非常非常非常非常非常喜爱{0}".format(course)}
            line = "{0}\t{1}\t{2}\t{3}\n" \
                .format(model['s_no'],
                        model['course'],
                        model['op_datetime'],
                        model['reason'])
            fp.write(line)
# 循环创建记录，一共是40000*500=2千万记录
for i in xrange(1, 501):
    # starttime = datetime.datetime.now() # create_student_dict 转换成dataframe格式，并注册临时表temp_student
    create_student_sc_dict(i)