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


### SQL转为MR的过程
整个编译过程分为6步
1. Antlr定义SQL的语法规则，完成SQL的词法、语法分析，将SQL转为抽象语法树AST Tree
2. 遍历AST，抽象出查询的基本组成单元QueryBlock
3. 遍历QueryBlock，翻译为执行操作树Operator Tree
4. 逻辑层优化器对OperatorTree变换，合并不必要的ReduceSinkOperator，减少shuffle数据量
5. 遍历OperatorTree，翻译为MR任务
6. 物理层优化器进行MR任务的变换，生成最终的执行计划

#### 1. 