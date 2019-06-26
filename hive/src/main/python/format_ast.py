# -*- encoding: utf-8 -*-
"""
Created on 10:06 2019/6/26

@author: fancyChuan
@email: 1247375074@qq.com

格式化抽象语法树
"""

import sys

# explain select key from kv mykv join test mytest on (mykv.key == mytest.id);

original_str = """(TOK_QUERY (TOK_FROM (TOK_LEFTOUTERJOIN (TOK_SUBQUERY (TOK_QUERY (TOK_FROM (TOK_TABREF (TOK_TABNAME dim_city))) (TOK_INSERT (TOK_DESTINATION (TOK_DIR TOK_TMP_FILE)) (TOK_SELECT (TOK_SELEXPR (TOK_TABLE_OR_COL city_name)) (TOK_SELEXPR (TOK_TABLE_OR_COL city_id)) (TOK_SELEXPR (TOK_TABLE_OR_COL pt))) (TOK_WHERE (AND (= (TOK_TABLE_OR_COL pt) '$yesday') (= (TOK_TABLE_OR_COL level) 2))) (TOK_GROUPBY (TOK_TABLE_OR_COL city_name) (TOK_TABLE_OR_COL city_id) (TOK_TABLE_OR_COL pt)))) b) (TOK_SUBQUERY (TOK_QUERY (TOK_FROM (TOK_TABREF (TOK_TABNAME dw_dri_wide_sheet))) (TOK_INSERT (TOK_DESTINATION (TOK_DIR TOK_TMP_FILE)) (TOK_SELECT (TOK_SELEXPR (TOK_TABLE_OR_COL city_id)) (TOK_SELEXPR (TOK_TABLE_OR_COL pt)) (TOK_SELEXPR (TOK_FUNCTIONDI count (TOK_FUNCTION when (= (TOK_FUNCTION to_date (TOK_TABLE_OR_COL last_sucgrabord_time)) '$data_desc') (TOK_TABLE_OR_COL dri_id))) last1_dri_cnt) (TOK_SELEXPR (TOK_FUNCTIONDI count (TOK_FUNCTION when (and (> (TOK_FUNCTION to_date (TOK_TABLE_OR_COL last_sucgrabord_time)) (TOK_FUNCTION date_sub '$data_desc' 7)) (<= (TOK_FUNCTION to_date (TOK_TABLE_OR_COL last_sucgrabord_time)) '$data_desc')) (TOK_TABLE_OR_COL dri_id))) last7_dri_cnt)) (TOK_WHERE (and (= (TOK_TABLE_OR_COL pt) '$data_desc') (TOK_FUNCTION TOK_ISNOTNULL (TOK_TABLE_OR_COL last_sucgrabord_time)))) (TOK_GROUPBY (TOK_TABLE_OR_COL city_id) (TOK_TABLE_OR_COL pt)))) a) (= (. (TOK_TABLE_OR_COL a) city_id) (. (TOK_TABLE_OR_COL b) city_id)))) (TOK_INSERT (TOK_DESTINATION (TOK_TAB (TOK_TABNAME test kd_st_kpi_dri_active_day_city_bi))) (TOK_SELECT (TOK_SELEXPR (. (TOK_TABLE_OR_COL b) city_name)) (TOK_SELEXPR (. (TOK_TABLE_OR_COL b) city_id)) (TOK_SELEXPR (TOK_FUNCTION nvl (TOK_TABLE_OR_COL last1_dri_cnt) 0)) (TOK_SELEXPR (TOK_FUNCTION nvl (TOK_TABLE_OR_COL last7_dri_cnt) 0)) (TOK_SELEXPR (. (TOK_TABLE_OR_COL b) pt))))) <EOF>"""

tmp_str = original_str.strip().replace('\n', '')


def my_print(mystr):
    sys.stdout.write(mystr)


def print_indent(indent_level):
    for i in range(indent_level):
        my_print(' ' * 4)


indent_level = 0
for char in tmp_str:
    if char == '(':
        # 如果是左括号,先换行,然后打印缩进+(
        my_print('\n')
        print_indent(indent_level)
        my_print(char)
        indent_level += 1
    elif char == ')':
        # 如果是右括号,先打印),再换行,打印下一级别的缩进
        indent_level -= 1
        my_print(char)
        my_print('\n')
        print_indent(indent_level - 1)
    else:
        # 其他的直接打印出来
        my_print(char)