package learningSpark.sparkStreaming;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

public class Main {

    private static SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("SparkStreamingLearning");


    public static void main(String[] args) {
        helloStreaming();
    }


    public static void helloStreaming() {
        // 创建streaming sc ，设置处理新数据的批次间隔为1s
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
        // 监听本地7777端口，收到的数据为 DStream
        JavaDStream<String> lines = jssc.socketTextStream("localhost", 7777);
        JavaDStream<String> errorLines = lines.filter(line -> line.contains("error"));
        errorLines.print();

        // 启动streaming，并等待作业完成
        jssc.start();
        try {
            // 数据处理会在另一个线程中进行，需要等待执行完成
            jssc.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
