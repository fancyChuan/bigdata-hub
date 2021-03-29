

小白按过往写PL-SQL和T-SQL的经验，总结了3条经验：
- 通过改写SQL，实现对计算引擎执行过程的干预；
    - 使用grouping sets代替union
    - 分解count(distinct)的SQL优化（使用子查询）
```
--改写前的代码段
    select ＊ from(
        select s_age, s_sex, count(1) num, 
        from student_tb_orc
        group by s_age, s_sex
        union all
        select s_age, null s_sex, count(1) num
        from student_tb_orc
        group by s_age
    ) a
--改写后的代码段
    select s_age, s_sex, count(1) num
    from student_tb_orc
    group by s_age, s_sex
    grouping sets((s_age), (s_age, s_sex))
;

--该案例演绎的是用子查询代替count（distinct），避免因数据倾斜导致的性能问题
--改写前的代码段
    --统计不同年龄段，考取的不同分数个数
    select s_age, count(distinct s_score) num
    from student_tb_orc
    group by s_age
--改写后的代码段
    select s_age, count(1) num
    from(
        select s_age, s_score, count(1) num
        group by s_age, s_score
    ) a
```
- 通过SQL-hint语法，实现对计算引擎执行过程的干预；
```
-- map join()
-- streamtable()
```
- 通过数据库开放的一些配置开关，实现对计算引擎的干预。
```
- 开启向量化查询开关
set hive.vectorized.execution.enabled=true;
- 开启并行执行
set hive.exec.parallel=true;
set hive.exec.parallel.thread.number=2;
```