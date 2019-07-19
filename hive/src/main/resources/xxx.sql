INSERT OVERWRITE TABLE tmp.tb_insur_credit_loan_tmp PARTITION (data_src='finance_s')       --社保贷变量
select cust_mobile,cust_no,var_data,var_type,from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss') update_time  from (
--历史第一次贷款发放时间
SELECT
		c.pri_phone_md5 cust_mobile,
    c.id_code_md5 cust_no,
    MIN(payment_time)       var_data,   
    'XinDS_HistoryFirstTime' var_type
FROM
    ods_credit_loan.tn_fms_loan l
    join ods_lms.tb_contact c on l.cust_id=c.row_id
    where l.product_type='fast_loan'
GROUP BY c.pri_phone_md5,
    c.id_code_md5
    union all
--客户是否当前逾期
SELECT
		c.pri_phone_md5 cust_mobile,
    c.id_code_md5 cust_no,
    IF(sum(IF(repay_status=2, 1, 0))>=1,'是','否') var_data,    
    'XinDS_Overdue_Is'        var_type
FROM
    ods_credit_loan.tn_fms_loan l
    join ods_lms.tb_contact c on l.cust_id=c.row_id
    where l.product_type='fast_loan'
group by c.pri_phone_md5,c.id_code_md5
    union all
--客户未结支取金额
SELECT
		c.pri_phone_md5 cust_mobile,
    c.id_code_md5 cust_no,
    COALESCE(SUM(l.remain_capital),'0') var_data,           
    'XinDS_NoDrawAmount'       var_type 
FROM
    ods_credit_loan.tn_fms_loan l
    join ods_lms.tb_contact c on l.cust_id=c.row_id
    
WHERE l.payment_status=3 and l.repay_status!=3 and l.product_type='fast_loan'
GROUP BY c.pri_phone_md5,
    c.id_code_md5
union all
--历史最近一年借款的逾期天数合计
select 
c.pri_phone_md5 cust_mobile,
c.id_code_md5 cust_no,
COALESCE(SUM(p.accum_ovd_days), '0') var_data,
'XinDS_LastYearOverdueDay'    var_type
from ods_credit_loan.tn_fms_repay_plan p
  LEFT JOIN ods_credit_loan.tn_fms_loan l
    ON p.loan_id=l.id
    join ods_lms.tb_contact c on l.cust_id=c.row_id
WHERE
   to_date(l.payment_time)>=DATE_SUB(from_unixtime(unix_timestamp(),'yyyy-MM-dd'), 365)
   and l.product_type='fast_loan'
    group by c.pri_phone_md5,c.id_code_md5
union all
--历史最近一年借款的最长逾期天数
select 
c.pri_phone_md5 cust_mobile,
c.id_code_md5 cust_no,
COALESCE(MAX(cast(p.accum_ovd_days as int)), '0') var_data,
'XinDS_LoanLongOverDay_365'    var_type
from ods_credit_loan.tn_fms_repay_plan p
  LEFT JOIN ods_credit_loan.tn_fms_loan l
    ON p.loan_id=l.id
    join ods_lms.tb_contact c on l.cust_id=c.row_id
WHERE
    to_date(l.payment_time)>=DATE_SUB(from_unixtime(unix_timestamp(),'yyyy-MM-dd'), 365)
    and l.product_type='fast_loan'
    group by c.pri_phone_md5,c.id_code_md5
union all
--历史最近3个月借款的逾期天数合计

select 
c.pri_phone_md5 cust_mobile,
c.id_code_md5 cust_no,
COALESCE(SUM(p.accum_ovd_days), '0') var_data,
'XinDS_LoanOverDay_90'    var_type
from ods_credit_loan.tn_fms_repay_plan p
  LEFT JOIN ods_credit_loan.tn_fms_loan l
    ON p.loan_id=l.id
    join ods_lms.tb_contact c on l.cust_id=c.row_id
WHERE
    to_date(l.payment_time)>=DATE_SUB(from_unixtime(unix_timestamp(),'yyyy-MM-dd'), 90)
    and l.product_type='fast_loan'
    group by c.pri_phone_md5,c.id_code_md5
union all
--历史最近3个月借款的最长逾期天数
select 
c.pri_phone_md5 cust_mobile,
c.id_code_md5 cust_no,
COALESCE(MAX(cast(p.accum_ovd_days as int)), '0') var_data,
'XinDS_LoanLongOverDay_90'    var_type
from ods_credit_loan.tn_fms_repay_plan p
  LEFT JOIN ods_credit_loan.tn_fms_loan l
    ON p.loan_id=l.id
    join ods_lms.tb_contact c on l.cust_id=c.row_id
WHERE
    to_date(l.payment_time)>=DATE_SUB(from_unixtime(unix_timestamp(),'yyyy-MM-dd'), 90)
    and l.product_type='fast_loan'
    group by c.pri_phone_md5,c.id_code_md5
    
union all

--历史最近1个月借款的逾期天数合计 
select 
c.pri_phone_md5 cust_mobile,
c.id_code_md5 cust_no,
COALESCE(SUM(p.accum_ovd_days), '0') var_data,
'XinDS_LoanOverDay_30'    var_type
from ods_credit_loan.tn_fms_repay_plan p
  LEFT JOIN ods_credit_loan.tn_fms_loan l
    ON p.loan_id=l.id
    join ods_lms.tb_contact c on l.cust_id=c.row_id
WHERE
    to_date(l.payment_time)>=DATE_SUB(from_unixtime(unix_timestamp(),'yyyy-MM-dd'), 30)
    and l.product_type='fast_loan'
    group by c.pri_phone_md5,c.id_code_md5
union all
--历史最近1个月借款的最长逾期天数
select 
c.pri_phone_md5 cust_mobile,
c.id_code_md5 cust_no,
COALESCE(SUM(p.accum_ovd_days), '0') var_data,
'XinDS_LoanLongOverDay_30'    var_type
from ods_credit_loan.tn_fms_repay_plan p
  LEFT JOIN ods_credit_loan.tn_fms_loan l
    ON p.loan_id=l.id
    join ods_lms.tb_contact c on l.cust_id=c.row_id
WHERE
    to_date(l.payment_time)>=DATE_SUB(from_unixtime(unix_timestamp(),'yyyy-MM-dd'), 30)
    and l.product_type='fast_loan'
    group by c.pri_phone_md5,c.id_code_md5
union all
--上一笔贷款逾期天数
SELECT
		t.pri_phone_md5 cust_mobile,
    t.cust_no cust_no,
    COALESCE(SUM(p.accum_ovd_days), '0') var_data,                
    'XinDS_PreLoanOverDay'        var_type
FROM
    (
        SELECT
        		c.pri_phone_md5,
            c.id_code_md5 as cust_no,
            MAX(cast(id as int)) max_loan_id
        FROM
            ods_credit_loan.tn_fms_loan l
            join ods_lms.tb_contact c on l.cust_id=c.row_id
        WHERE
            payment_status=3 and l.product_type='fast_loan'
        GROUP BY c.pri_phone_md5,
            c.id_code_md5) as t

left join
ods_credit_loan.tn_fms_repay_plan p
    on t.max_loan_id=p.loan_id

GROUP BY t.pri_phone_md5,
    t.cust_no
union all
--上一笔贷款结清日期    
SELECT
		c.pri_phone_md5 cust_mobile,
    c.id_code_md5 cust_no,
    MAX(settle_date)      var_data,                    
    'XinDS_PreLoanEndTime' var_type
FROM
    ods_credit_loan.tn_fms_loan l
INNER JOIN
    (
        SELECT
            cust_id,
            MAX(cast(id as int)) max_loan_id
        FROM
            ods_credit_loan.tn_fms_loan d
            
        WHERE
            repay_status=3 and d.product_type='fast_loan'
        GROUP BY
            cust_id) as t
ON
    l.id = t.max_loan_id
AND l.cust_id=t.cust_id
join ods_lms.tb_contact c on l.cust_id=c.row_id
GROUP BY c.pri_phone_md5,
    c.id_code_md5
union all
--上一笔贷款.滞纳金
SELECT
		l.pri_phone_md5 cust_mobile,
    l.cust_no cust_no,
    COALESCE(SUM(p.ovd_tot_interest)+SUM(p.ovd_tot_fee), '0') var_data,                
    'XinDS_PreLoan_LateFee'      var_type
FROM
    (
        SELECT
        		c.pri_phone_md5,
            c.id_code_md5 as cust_no,
            MAX(cast(id as int)) max_loan_id
        FROM
            ods_credit_loan.tn_fms_loan t
            join ods_lms.tb_contact c on t.cust_id=c.row_id
        WHERE
            payment_status=3 and t.product_type='fast_loan'
        GROUP BY c.pri_phone_md5,
            c.id_code_md5) as l
LEFT JOIN
ods_credit_loan.tn_fms_repay_plan p
ON
    l.max_loan_id = p.loan_id
GROUP BY l.pri_phone_md5,
    l.cust_no
union all
--上一笔贷款.贷款本金
SELECT
		c.pri_phone_md5 cust_mobile,
    c.id_code_md5 cust_no,
    COALESCE(SUM(loan_amt), '0') var_data,                   
    'XinDS_PreLoan_LoanPrin'        var_type
FROM
    ods_credit_loan.tn_fms_loan l
INNER JOIN
    (
        SELECT
            cust_id,
            MAX(cast(id as int)) max_loan_id
        FROM
            ods_credit_loan.tn_fms_loan d
            
        WHERE
            payment_status=3 and d.product_type='fast_loan'
        GROUP BY
            cust_id) as t
ON
    l.id = t.max_loan_id
AND l.cust_id=t.cust_id
join ods_lms.tb_contact c on l.cust_id=c.row_id
GROUP BY c.pri_phone_md5,
    c.id_code_md5
union all
--上一笔贷款.催收费
SELECT
		l.pri_phone_md5 cust_mobile,
    l.cust_no cust_no,
    COALESCE(SUM(tot_money), '0') var_data,                --催收费取实收 
    'XinDS_PreLoan_ChargeFee'      var_type
FROM

    (
        SELECT
        		c.pri_phone_md5,
            c.id_code_md5 cust_no,
            MAX(cast(id as int)) max_loan_id
        FROM
            ods_credit_loan.tn_fms_loan t
            join ods_lms.tb_contact c on t.cust_id=c.row_id
        WHERE
            payment_status=3 and t.product_type='fast_loan'
        GROUP BY c.pri_phone_md5,
            c.id_code_md5) as l
left join  ods_credit_loan.tn_fms_repay_plan r on l.max_loan_id=r.loan_id
JOIN
    ods_credit_loan.tn_fms_repay_fee f
ON
    r.id = f.repay_id
JOIN
    ods_credit_loan.tn_fms_repay_fee_config c
ON
    f.config_id=c.id and c.label='VISIT_FEE'  and c.id=39
GROUP BY l.pri_phone_md5,
    l.cust_no

union all

--信贷.上一笔放款日期
	SELECT
		c.pri_phone_md5 cust_mobile,
    c.id_code_md5 cust_no,
    MAX(payment_time) var_data,   
    'XinDS_LastCreditTime' var_type
FROM
    ods_credit_loan.tn_fms_loan l
join ods_lms.tb_contact c on l.cust_id=c.row_id
where l.product_type='fast_loan'
GROUP BY c.pri_phone_md5,
    c.id_code_md5
) tmp