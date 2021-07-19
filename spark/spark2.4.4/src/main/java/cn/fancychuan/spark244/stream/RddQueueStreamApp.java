package cn.fancychuan.spark244.stream;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;


public class RddQueueStreamApp {

    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("RddQueueStreamApp");
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));


    }
}
