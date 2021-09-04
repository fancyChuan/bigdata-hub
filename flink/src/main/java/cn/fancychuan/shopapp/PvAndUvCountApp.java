package cn.fancychuan.shopapp;


import cn.fancychuan.shopapp.bean.UserBehaviorBean;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashSet;


/**
 * 计算PV和UV
 *  注意：
 *  1. 对于实时的流来说，结果也是一个个的流，如果用可视化来展示，那么随着时间的推移，图形会动的
 *      而对于批计算来说，这个文件的PV和UV最终就是一个值
 *  2. 因此，此demo的关键是构建Tuple2("pv", 1)和Tuple2("uv", userID)
 */
public class PvAndUvCountApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> inputStream = env.readTextFile(PvAndUvCountApp.class.getClassLoader().getResource("UserBehavior.csv").getPath());
        SingleOutputStreamOperator<UserBehaviorBean> userBehaviorStream = inputStream.map(new MapFunction<String, UserBehaviorBean>() {
            @Override
            public UserBehaviorBean map(String s) throws Exception {
                String[] items = s.split(",");
                UserBehaviorBean userBehaviorBean = new UserBehaviorBean(
                        Long.parseLong(items[0]),
                        Long.parseLong(items[1]),
                        Integer.parseInt(items[2]),
                        items[3],
                        Long.parseLong(items[4])
                );
                return userBehaviorBean;
            }
        });

        SingleOutputStreamOperator<UserBehaviorBean> pvStream = userBehaviorStream.filter(new FilterFunction<UserBehaviorBean>() {
            @Override
            public boolean filter(UserBehaviorBean userBehaviorBean) throws Exception {
                return userBehaviorBean.getBehavior().equals("pv");
            }
        });

        // 计算PV
        SingleOutputStreamOperator<Tuple2<String, Integer>> pvCountResult = pvStream.map(new MapFunction<UserBehaviorBean, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(UserBehaviorBean userBehaviorBean) throws Exception {
                        // return new Tuple2(userBehaviorBean.getItemId(), 1);
                        return new Tuple2("pv", 1);
                    }
                })
                .keyBy(tuple2 -> tuple2.f0)
                .sum(1);

        pvCountResult.print("PV");
        // 计算UV
        KeyedStream<Tuple2<String, Long>, String> keyedStream = pvStream.map(new MapFunction<UserBehaviorBean, Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> map(UserBehaviorBean userBehaviorBean) throws Exception {
                return new Tuple2<>("uv", userBehaviorBean.getUserId());
            }
        }).keyBy(new KeySelector<Tuple2<String, Long>, String>() {
            @Override
            public String getKey(Tuple2<String, Long> stringLongTuple2) throws Exception {
                return stringLongTuple2.f0;
            }
        });
        SingleOutputStreamOperator<Integer> uvCountResult = keyedStream.process(new KeyedProcessFunction<String, Tuple2<String, Long>, Integer>() {
            HashSet<Long> userSet = new HashSet<>();

            @Override
            public void processElement(Tuple2<String, Long> value, KeyedProcessFunction<String, Tuple2<String, Long>, Integer>.Context ctx, Collector<Integer> out) throws Exception {
                userSet.add(value.f1);
                out.collect(userSet.size());
            }
        });
        uvCountResult.print("UV");

        env.execute();
    }
}
