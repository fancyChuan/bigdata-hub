```
explain 
select name, count(1) cnt from zxc.user_login_log group by name ;
```
得到执行计划：
```
STAGE DEPENDENCIES:
  Stage-1 is a root stage
  Stage-0 depends on stages: Stage-1

STAGE PLANS:
  Stage: Stage-1
    Map Reduce
      Map Operator Tree:
          TableScan
            alias: user_login_log
            Statistics: Num rows: 414514 Data size: 41451436 Basic stats: COMPLETE Column stats: NONE
            Select Operator
              expressions: name (type: string)
              outputColumnNames: name
              Statistics: Num rows: 414514 Data size: 41451436 Basic stats: COMPLETE Column stats: NONE
              Group By Operator
                aggregations: count(1)
                keys: name (type: string)
                mode: hash
                outputColumnNames: _col0, _col1
                Statistics: Num rows: 414514 Data size: 41451436 Basic stats: COMPLETE Column stats: NONE
                Reduce Output Operator
                  key expressions: _col0 (type: string)
                  sort order: +
                  Map-reduce partition columns: _col0 (type: string)
                  Statistics: Num rows: 414514 Data size: 41451436 Basic stats: COMPLETE Column stats: NONE
                  value expressions: _col1 (type: bigint)
      Reduce Operator Tree:
        Group By Operator
          aggregations: count(VALUE._col0)
          keys: KEY._col0 (type: string)
          mode: mergepartial
          outputColumnNames: _col0, _col1
          Statistics: Num rows: 207257 Data size: 20725718 Basic stats: COMPLETE Column stats: NONE
          File Output Operator
            compressed: false
            Statistics: Num rows: 207257 Data size: 20725718 Basic stats: COMPLETE Column stats: NONE
            table:
                input format: org.apache.hadoop.mapred.TextInputFormat
                output format: org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat
                serde: org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe

  Stage: Stage-0
    Fetch Operator
      limit: -1
      Processor Tree:
        ListSink
```

设置使用spark引擎
``` set hive.execution.engine=spark;```
得到的执行计划如下：
```
STAGE DEPENDENCIES:
  Stage-1 is a root stage
  Stage-0 depends on stages: Stage-1

STAGE PLANS:
  Stage: Stage-1
    Spark
      Edges:
        Reducer 2 <- Map 1 (GROUP, 2)
      DagName: hive_20210328162121_04f3318f-2227-4ae2-8efc-782e22f371cf:1
      Vertices:
        Map 1 
            Map Operator Tree:
                TableScan
                  alias: user_login_log
                  Statistics: Num rows: 414514 Data size: 41451436 Basic stats: COMPLETE Column stats: NONE
                  Select Operator
                    expressions: name (type: string)
                    outputColumnNames: name
                    Statistics: Num rows: 414514 Data size: 41451436 Basic stats: COMPLETE Column stats: NONE
                    Group By Operator
                      aggregations: count(1)
                      keys: name (type: string)
                      mode: hash
                      outputColumnNames: _col0, _col1
                      Statistics: Num rows: 414514 Data size: 41451436 Basic stats: COMPLETE Column stats: NONE
                      Reduce Output Operator
                        key expressions: _col0 (type: string)
                        sort order: +
                        Map-reduce partition columns: _col0 (type: string)
                        Statistics: Num rows: 414514 Data size: 41451436 Basic stats: COMPLETE Column stats: NONE
                        value expressions: _col1 (type: bigint)
        Reducer 2 
            Reduce Operator Tree:
              Group By Operator
                aggregations: count(VALUE._col0)
                keys: KEY._col0 (type: string)
                mode: mergepartial
                outputColumnNames: _col0, _col1
                Statistics: Num rows: 207257 Data size: 20725718 Basic stats: COMPLETE Column stats: NONE
                File Output Operator
                  compressed: false
                  Statistics: Num rows: 207257 Data size: 20725718 Basic stats: COMPLETE Column stats: NONE
                  table:
                      input format: org.apache.hadoop.mapred.TextInputFormat
                      output format: org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat
                      serde: org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe

  Stage: Stage-0
    Fetch Operator
      limit: -1
      Processor Tree:
        ListSink
```