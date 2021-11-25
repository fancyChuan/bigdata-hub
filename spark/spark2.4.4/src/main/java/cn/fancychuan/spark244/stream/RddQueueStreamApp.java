package cn.fancychuan.spark244.stream;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class RddQueueStreamApp {

    public static void main(String[] args) throws InterruptedException {
        SparkConf sparkConf = new SparkConf().setAppName("RddQueueStreamApp").setMaster("local[*]");
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));
        JavaSparkContext sc = jssc.sparkContext();

        Queue<JavaRDD<Integer>> queue = new LinkedList<>();

        JavaInputDStream<Integer> inputDStream = jssc.queueStream(queue, true);
        inputDStream.reduce((a,b) -> {
            System.out.println(a + "======" +b);
            return a+b;
        }).print();

        jssc.start();

        for (int i = 0; i < 10; i++) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int j = 0; j < 100; j++) {
                list.add(j);
            }
            JavaRDD<Integer> rdd = sc.parallelize(list);
            queue.add(rdd);
            Thread.sleep(2000);
        }

        jssc.awaitTermination();
    }
}
