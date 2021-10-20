--由于数据倾斜，先找出近90天买家数超过10000的卖家
insert overwrite table tmp_table_B
select
    m.seller_id,
    n.s_level,
from(
        select
            seller_id
        from(
                select
                    seller_id,
                    count(buyer_id) as byr_cnt
                from
                    table_A
                group by
                    seller_id
            ) a
        where
                a.byr_cnt>10000
    ) m
left outer join(
    select
        user_id,
        s_level,
    from
        table_B
) n
on m.seller_id=n.user_id;



--对于90天买家数超过10000的卖家直接mapjoin，对于其他卖家正常join即可
select
    m.buyer_id
     ,sum(pay_cnt_90d)                                      as pay_cnt_90d
     ,sum(case when m.s_level=0  then pay_cnt_90d  end) as pay_cnt_90d_s0
     ,sum(case when m.s_level=1  then pay_cnt_90d  end) as pay_cnt_90d_s1
     ,sum(case when m.s_level=2  then pay_cnt_90d  end) as pay_cnt_90d_s2
     ,sum(case when m.s_level=3  then pay_cnt_90d  end) as pay_cnt_90d_s3
     ,sum(case when m.s_level=4  then pay_cnt_90d  end) as pay_cnt_90d_s4
     ,sum(case when m.s_level=5  then pay_cnt_90d  end) as pay_cnt_90d_s5
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
                    select seller_id, a.s_level
                    from  table_A a
                    left outer join tmp_table_B b
                    on a.user_id=b.seller_id
                    where b.seller_id is null
                ) b
        on a.seller_id=b.seller_id

        union all

        select /＊+mapjoin(b)＊/
                    a.buyer_id, a.seller_id, b.s_level, a.pay_cnt_90d
        from
            (
            select buyer_id, seller_id, pay_cnt_90d
            from  table_A
            ) a
            join
            (   -- todo: 这部分代码《实时和离线大数据开发实战》 https://weread.qq.com/web/reader/7e332cb05e45157e3d0ec59ke3632bd0222e369853df322
                -- 要改是有问题的
            select seller_id, s_level
            from  table_B
            ) b
        on a.seller_id=b.seller_id
    ) m group by m.buyer_id
    ) m
group by m.buyer_id