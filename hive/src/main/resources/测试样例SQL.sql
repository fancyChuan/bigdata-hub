-- 1.加载本地文件到hive表
load data local inpath '/opt/datas/emp.txt' into table default.emp
;
-- 2.加载hdfs文件到hive中
load data inpath '/user/beifeng/hive/datas/emp.txt' overwrite into table default.emp
;
-- 3.加载数据覆盖表中已有的数据
load data inpath '/user/beifeng/hive/datas/emp.txt' into table default.emp
;
-- 4.创建表是通过select加载
create table default.emp_ci as select * FROM  default.emp
;
-- 5.创建表是通过insert加载
-- create table default.emp_ci like emp ;
insert into table default.emp_ci select * from default.emp
;
-- 6.创建表的时候通过location指定加载
create EXTERNAL table IF NOT EXISTS default.emp_ext2(
empno int,
ename string,
deptno int
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
location '/user/beifeng/hive/warehouse/emp_ext2';