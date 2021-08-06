package cn.fancychuan;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.util.ArrayList;
import java.util.HashMap;

public class JavaSinkEsApp {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> inputStream = env.readTextFile("E:\\JavaWorkshop\\bigdata-learn\\flink\\src\\main\\resources\\sensor.txt");
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] items = line.split(",");
            return new SensorReading(items[0], Long.parseLong(items[1]), new Double(items[2]));
        });

        // es 的http hosts
        ArrayList<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost("hadoop101", 9200));
        // 添加es sink
        dataStream.addSink(new ElasticsearchSink.Builder<SensorReading>(httpHosts, new MyEsSinkFunction()).build());
    }

    private static class MyEsSinkFunction implements ElasticsearchSinkFunction<SensorReading> {
        @Override
        public void process(SensorReading sensorReading, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
            HashMap<String, String> dataSource = new HashMap<>();
            dataSource.put("id", sensorReading.getId());
            dataSource.put("ts", sensorReading.getTimestamp().toString());
            dataSource.put("temp", sensorReading.getTemperature().toString());

            IndexRequest indexRequest = Requests.indexRequest()
                    .index("sensor")
                    .type("readingData")
                    .source(dataSource);

            requestIndexer.add(indexRequest);
        }
    }
}
