## SQL分解与功能增强

```
# sql
--------------------------------
select sum(投资金额) from ($view$) src
where 期限>=1 and 期限<=6
group by 产品类型，渠道
order by sum(投资金额) desc
---------------------------------
# 对应的json参数：
{
	"groups": [
		"产品类型",
		"渠道"
	],
	"aggregators": [
		{
			"column": "投资金额",
			"func": "sum"
		}
	],
	"filters": [
		{
			"type": "relation",
			"value": "and",
			"children": [
				{
					"name": "期限",
					"type": "filter",
					"value": 1,
					"operator": ">=",
					"sqlType": "DECIMAL"
				},
				{
					"name": "期限",
					"type": "filter",
					"value": 6,
					"operator": ">=",
					"sqlType": "DECIMAL"
				}
			]
		}
	],
	"orders": [
		{
			"column": "sum(投资金额)",
			"direction": "desc"
		}
	],
	"pageNo": 1,
	"pageSize": 20,
	"nativeQuery": false,
	"cache": false,
	"expired": 0,
	"flush": false
}
```

类型：
1. 指标加减乘除
	select 渠道, sum(金额)/count(distinct userid) as 人均金额 from xxx group by 渠道
2. 自定义字段
	- 2.1 普通字段加工
		select substr(字段A, 0, 6), sum(B) from table group by substr(字段A, 0, 6)
	- 2.2 分组字段加工
		select case when city in ('广州', '深圳') then '广东省内' else '广东省外' end as cityType, sum(金额) from table 
		group by case when city in ('广州', '深圳') then '广东省内' else '广东省外' end
	- 2.3 自定义函数支持