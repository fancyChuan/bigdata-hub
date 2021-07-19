package cn.fancychuan.quickstart;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.util.Collector;

/**
 * 批处理的wordcount
 */
public class WordCountApp {
    public static void main(String[] args) {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        String inputPath = "log4j.properties";
        DataSet<String> inputDataSet = env.readTextFile(inputPath);
        inputDataSet.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                String[] split = s.split(".");

            }
        });
    }
}
