## HiveSQL编译过程详解


SQL种类及对应MapReduce过程
```
# 1. join
select u.name, o.orderid from order o join user u on o.uid = u.uid;
# 2. group by
select rank, isonline, count(*) from city group by rank, isonline;
# 3.1 distinct 单字段
select dealid, count(distinct uid) num from order group by dealid;
# 3.2 distinct 多字段
select dealid, count(distinct uid), count(distinct date) from order group by dealid;
```

复杂SQL的执行过程
```
select COALESCE(T.date_day,T2.shijian) AS shijian
          , COALESCE(T1.user_id,T2.user_id) AS user_id
          , NVL(T1.money,0) AS money
          , NVL(T2.total,0) AS total 
FROM (SELECT DISTINCT TO_DATE(date_day) AS date_day FROM common_db.tb_common_calendar where date_day >='2012-07-20' and date_day <=current_date) T 
  LEFT JOIN (
        SELECT user_id
         , FROM_UNIXTIME(CAST(addtime AS BIGINT), 'yyyy-MM-dd') AS shijian
         , SUM(NVL(money,0)) AS money
        FROM ods_touna.dw_account_recharge a
        WHERE status = 1 AND exists (SELECT 1 FROM app_db.tn_trust_user b WHERE total_collection>0 AND b.user_id=a.user_id)
        GROUP BY user_id,FROM_UNIXTIME(CAST(addtime AS BIGINT), 'yyyy-MM-dd')
  ) T1 ON T.date_day = T1.shijian
  FULL JOIN (
      SELECT user_id
       , FROM_UNIXTIME(success_time, 'yyyy-MM-dd') AS shijian
       , SUM(NVL(total,0)) AS total
      FROM ods_touna.dw_account_cash a
      WHERE status = 6 AND exists (SELECT 1 FROM app_db.tn_trust_user b WHERE total_collection>0 AND b.user_id=a.user_id)
      GROUP BY user_id,FROM_UNIXTIME(success_time, 'yyyy-MM-dd') 
  )T2 ON T1.date_day = T2.shijian AND T1.user_id = T2.user_id
```

### SQL转为MR的过程
整个编译过程分为6步
1. Antlr定义SQL的语法规则，完成SQL的词法、语法分析，将SQL转为抽象语法树AST Tree
2. 遍历AST，抽象出查询的基本组成单元QueryBlock
3. 遍历QueryBlock，翻译为执行操作树Operator Tree
4. 逻辑层优化器对OperatorTree变换，合并不必要的ReduceSinkOperator，减少shuffle数据量
5. 遍历OperatorTree，翻译为MR任务
6. 物理层优化器进行MR任务的变换，生成最终的执行计划

#### 1. 






### 参考资料
1. [使用Hive API分析HQL的执行计划、Job数量和表的血缘关系 – lxw的大数据田地](http://lxw1234.com/archives/2015/09/476.htm)
2. [【美团技术分享】sql语句转换成mapreduce - Mr.Ming2 - 博客园](https://www.cnblogs.com/Dhouse/p/7132476.html)
3. [HiveSQL编译过程详解_逸卿的专栏-CSDN博客](https://blog.csdn.net/u010102540/article/details/38337439)
