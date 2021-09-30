package cn.fancychuan.shopapp;

import cn.fancychuan.shopapp.bean.UserBehaviorBean;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 实时热门商品统计：每隔5分钟输出最近一小时内点击量最多的前N个商品
 */
public class App05PvWithWindow {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

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

        pvStream.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<UserBehaviorBean>() {
            @Override
            public long extractAscendingTimestamp(UserBehaviorBean element) {
                return element.getTimestamp() * 1000L;
            }
        }).keyBy(UserBehaviorBean::getItemId)
                .timeWindow(Time.hours(1), Time.minutes(5));

    }
}
