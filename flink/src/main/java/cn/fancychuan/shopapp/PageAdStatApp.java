package cn.fancychuan.shopapp;

import cn.fancychuan.shopapp.bean.AdClickLog;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 页面广告分析
 *  页面广告点击量实时统计：根据省份和广告进行分组统计
 *      - 使用tuple2，将省份和广告拼接
 */
public class PageAdStatApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> inputStream = env.readTextFile(PageAdStatApp.class.getClassLoader().getResource("AdClickLog.csv").getPath());
        SingleOutputStreamOperator<AdClickLog> dataStream = inputStream.map(new MapFunction<String, AdClickLog>() {
            @Override
            public AdClickLog map(String s) throws Exception {
                String[] items = s.split(",");
                return new AdClickLog(
                        Long.parseLong(items[0]),
                        Long.parseLong(items[1]),
                        items[2],
                        items[3],
                        Long.parseLong(items[4])
                );
            }
        });

        SingleOutputStreamOperator<Tuple2<String, Integer>> provinceAdResult =
                dataStream.map(new MapFunction<AdClickLog, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(AdClickLog adClickLog) throws Exception {
                        return new Tuple2<>(adClickLog.getProvince() + "_" + adClickLog.getAdId(), 1);
                    }
                }).keyBy(tuple2 -> tuple2.f0)
                .sum(1);
        provinceAdResult.print("provinceAdClick");

        env.execute();

    }
}
