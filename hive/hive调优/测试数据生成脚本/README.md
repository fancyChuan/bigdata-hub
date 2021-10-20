## 测试数据生产执行代码

### 学生信息表（student_tb_txt）


```
-- hive建表语句
create table if not exists forlearn.student_tb_txt(
  s_no string comment '学号',
  s_name string comment '姓名',
  s_birth string comment '生日',
  s_age bigint  comment '年龄',
  s_sex string comment '性别',
  s_score bigint comment '综合能力得分',
  s_desc string comment '自我介绍'
)
row format delimited
fields terminated by '\t'
location '/user/appuser/forlearn/student_tb_txt/';
```

### 学生选课信息表（student_sc_tb_txt）


```
create table if not exists forlearn.student_sc_tb_txt(
  s_no string comment '学号',
  course string comment '课程名',
  op_datetime string comment '操作时间',
  reason  string comment '选课原因'
)
row format delimited
fields terminated by '\t'
location '/user/appuser/forlearn/student_sc_tb_txt';
```