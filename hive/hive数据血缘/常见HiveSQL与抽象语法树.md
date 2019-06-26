## HiveSQL与抽象语法树

- 普通select 语句
    - 一个TOK_QUERY下面有两个子节点：TOK_FROM，TOK_INSERT
    - TOK_FROM 源表信息
```
# select user_id, username from ods_touna.dw_user limit 10
TOK_QUERY
   TOK_FROM
      TOK_TABREF
         TOK_TABNAME
            ods_touna
            dw_user
   TOK_INSERT
      TOK_DESTINATION        # 虽然SQL会把结果显示到控制台，但实际上hive是把数据写入临时文件的
         TOK_DIR
            TOK_TMP_FILE
      TOK_SELECT
         TOK_SELEXPR
            TOK_TABLE_OR_COL
               user_id
         TOK_SELEXPR
            TOK_TABLE_OR_COL
               username
      TOK_LIMIT
         10
# ========== 执行计划 =============
STAGE DEPENDENCIES:
  Stage-0 is a root stage [FETCH]

STAGE PLANS:
  Stage: Stage-0
    Fetch Operator
      limit: 10
      Processor Tree:
        TableScan
          alias: dw_user
          GatherStats: false
          Select Operator
            expressions: user_id (type: int), username (type: varchar(100))
            outputColumnNames: _col0, _col1
            Limit
              Number of rows: 10
              ListSink
```