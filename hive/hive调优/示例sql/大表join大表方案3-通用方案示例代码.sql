select
    m.buyer_id
     ,sum(pay_cnt_90d)                                            as pay_cnt_90d
     ,sum(case when m.s_level=0     then pay_cnt_90d  end) as pay_cnt_90d_s0
     ,sum(case when m.s_level=1     then pay_cnt_90d  end) as pay_cnt_90d_s1
     ,sum(case when m.s_level=2     then pay_cnt_90d  end) as pay_cnt_90d_s2
     ,sum(case when m.s_level=3     then pay_cnt_90d  end) as pay_cnt_90d_s3
     ,sum(case when m.s_level=4     then pay_cnt_90d  end) as pay_cnt_90d_s4
     ,sum(case when m.s_level=5     then pay_cnt_90d  end) as pay_cnt_90d_s5

from
    (
        select
            a.buyer_id, a.seller_id, b.s_level, a.pay_cnt_90d
        from
            (
                select buyer_id, seller_id, pay_cnt_90d
                from  table_A
            ) a
                join
            (
                select /＊+mapjoin(members)＊/
                seller_id, s_level, member
                from  table_B
                    join
                    members
            ) b
            on a.seller_id=b.seller_id
                and mod(a.pay_cnt_90d,10)+1=b.number
    ) m
group by m.buyer_id