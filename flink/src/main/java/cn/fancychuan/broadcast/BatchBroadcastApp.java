package cn.fancychuan.broadcast;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BatchBroadcastApp {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        ArrayList<Tuple2<String, Integer>> broadData = new ArrayList<>();
        broadData.add(Tuple2.of("flink", 20));
        broadData.add(Tuple2.of("flink1", 21));
        broadData.add(Tuple2.of("flink2", 22));
        broadData.add(Tuple2.of("flink3", 23));

        DataSource<Tuple2<String, Integer>> broadDataSet = env.fromCollection(broadData);
        MapOperator<Tuple2<String, Integer>, HashMap<String, Integer>> toBroadcast = broadDataSet.map(new MapFunction<Tuple2<String, Integer>, HashMap<String, Integer>>() {
            @Override
            public HashMap<String, Integer> map(Tuple2<String, Integer> value) throws Exception {
                HashMap<String, Integer> res = new HashMap<>();
                res.put(value.f0, value.f1);
                return res;
            }
        });

        DataSource<String> dataSet = env.fromElements("sz", "flink", "flink2");

        DataSet<String> result = dataSet.map(new RichMapFunction<String, String>() {
            private List<HashMap<String, Integer>> broadCastMap = new ArrayList<HashMap<String, Integer>>();
            HashMap<String, Integer> allMap = new HashMap<String, Integer>();
            @Override
            public void open(Configuration parameters) throws Exception {
                // 获取广播数据
                broadCastMap = getRuntimeContext().getBroadcastVariable("broadCastMapName");
                for (HashMap<String, Integer> hashMap : broadCastMap) {
                    allMap.putAll(hashMap);
                }
            }

            @Override
            public String map(String value) throws Exception {
                Integer age = allMap.get(value);
                return value + "," + age;
            }
        }).withBroadcastSet(toBroadcast, "broadCastMapName");

        result.print();

    }
}
