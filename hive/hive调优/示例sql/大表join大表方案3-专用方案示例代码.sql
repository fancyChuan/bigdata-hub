select
    m.buyer_id
     ,sum(pay_cnt_90d)                                           as pay_cnt_90d
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
                select     /＊mapjoin(big)＊/
                        buyer_id, seller_id, pay_cnt_90d,
                        -- 这里判断是否是大卖家
                        if(big.seller_id is not null, concat(table_A.seller_id, 'rnd', cast(rand() ＊1000 as bigint), table_A.seller_id) as seller_id_joinkey
                from  table_A
                left outer join
                        --big表seller有重复，请注意一定要group by后再join，保证table_A的行数保持不变
                        (select seller_id from dim_big_seller   group by seller_id) big
                on table_A.seller_id=big.seller_id
            ) a
            join
            (
                select /＊+mapjoin(big)＊/
                        seller_id, s_level,
                        --big表的seller_id_joinkey生成逻辑和上面的生成逻辑一样
                        coalesce(seller_id_joinkey, table_B.seller_id) as seller_id_joinkey
                from  table_B
                left outer join
                    --table_B表join大卖家表后大卖家行数放大1000倍，其他卖家行数保持不变
                    (select seller_id, seller_id_joinkey from dim_big_seller) big
                on table_B.seller_id=big.seller_id
            ) b
                on a.seller_id_joinkey=b.seller_id_joinkey
            ) m
        group by m.buyer_id