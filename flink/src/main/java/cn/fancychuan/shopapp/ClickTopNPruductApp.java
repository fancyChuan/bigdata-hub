package cn.fancychuan.shopapp;

import cn.fancychuan.shopapp.bean.UserBehaviorBean;
import org.apache.flink.api.common.eventtime.AscendingTimestampsWatermarks;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * 需求：实时热门商品（每隔5分钟输出最近一小时内点击量最多的前N个产品
 */
public class ClickTopNPruductApp {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStreamSource<String> inputStream = env.readTextFile(ClickTopNPruductApp.class.getClassLoader().getResource("UserBehavior.csv").getPath());
        SingleOutputStreamOperator<UserBehaviorBean> dataStream =
                inputStream.map(new MapFunction<String, UserBehaviorBean>() {
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
                }).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<UserBehaviorBean>() {
                    // 使用了时间语义，一定要记得分配！
                    @Override
                    public long extractAscendingTimestamp(UserBehaviorBean element) {
                        return element.getTimestamp();
                    }
                });

        SingleOutputStreamOperator<UserBehaviorBean> userBehaviorStream = dataStream.filter((FilterFunction<UserBehaviorBean>) userBehaviorBean -> "pv".equals(userBehaviorBean.getBehavior()));

        userBehaviorStream.keyBy(UserBehaviorBean::getItemId)
                .timeWindow(Time.hours(1), Time.minutes(5))
                .aggregate(
                        new AggregateFunction<UserBehaviorBean, Long, Long>() {
                            @Override
                            public Long createAccumulator() {
                                return 0L;
                            }
                            @Override
                            public Long add(UserBehaviorBean userBehaviorBean, Long aLong) {
                                return aLong + 1;
                            }
                            @Override
                            public Long getResult(Long aLong) {
                                return aLong;
                            }
                            @Override
                            public Long merge(Long aLong, Long acc1) {
                                return acc1 + aLong;
                            }
                        },
                        new WindowFunction<Long, Object, Long, TimeWindow>() {
                            @Override
                            public void apply(Long aLong, TimeWindow window, Iterable<Long> input, Collector<Object> out) throws Exception {

                            }
                        });
    }
}
