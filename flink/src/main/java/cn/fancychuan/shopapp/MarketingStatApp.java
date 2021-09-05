package cn.fancychuan.shopapp;

import cn.fancychuan.shopapp.bean.MarketingUserBehavior;
import cn.fancychuan.shopapp.source.GenMarketingUserBehavior;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 市场营销相关指标统计
 *  1. APP市场推广统计 - 分渠道
 *      计算不同渠道用户不同行为的统计量
 *  2. APP市场推广统计 - 不分渠道
 *      计算用户不同行为的统计量
 */
public class MarketingStatApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<MarketingUserBehavior> dataStream = env.addSource(new GenMarketingUserBehavior());

        SingleOutputStreamOperator<Tuple2<String, Integer>> channelBehaviorResult =
                dataStream.map(new MapFunction<MarketingUserBehavior, Tuple2<String, Integer>>() {
                            @Override
                            public Tuple2<String, Integer> map(MarketingUserBehavior item) throws Exception {
                                return new Tuple2<String, Integer>(item.getChannel() + "_" + item.getBehavior(), 1);
                            }
                        })
                .keyBy(tuple -> tuple.f0)
                .sum(1);
        channelBehaviorResult.print("channelBehavior");

        SingleOutputStreamOperator<Tuple2<String, Integer>> behaviorResult =
                dataStream.map(new MapFunction<MarketingUserBehavior, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(MarketingUserBehavior item) throws Exception {
                        return Tuple2.of(item.getBehavior(), 1);
                    }
                }).keyBy(new KeySelector<Tuple2<String, Integer>, String>() {
                    @Override
                    public String getKey(Tuple2<String, Integer> tuple2) throws Exception {
                        return tuple2.f0;
                    }
                }).sum(1);
        behaviorResult.print("behaviorResult");

        env.execute();
    }
}
